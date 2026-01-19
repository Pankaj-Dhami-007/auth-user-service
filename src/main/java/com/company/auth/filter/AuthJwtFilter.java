package com.company.auth.filter;

import com.company.auth.security.jwt.JwtTokenValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class AuthJwtFilter extends OncePerRequestFilter {

    private final JwtTokenValidator jwtTokenValidator;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return !(
                path.startsWith("/auth/api/v1/refresh") ||
                        path.startsWith("/auth/api/v1/logout")
        );
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing Authorization header");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtTokenValidator.validateToken(token)) {
            log.warn("Invalid refresh token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (!jwtTokenValidator.isRefreshToken(token)) {
            log.warn("Access token used on refresh/logout");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Long userId =
                jwtTokenValidator.getUserIdFromRefreshToken(token);

        String jti = jwtTokenValidator.getJti(token);

        request.setAttribute("userId", userId);
        request.setAttribute("refreshToken", token);
        request.setAttribute("jti", jti);

        filterChain.doFilter(request, response);
    }
}

