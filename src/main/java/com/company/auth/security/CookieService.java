package com.company.auth.security;

import com.company.auth.config.JwtProperties;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CookieService {

    private final JwtProperties jwtProperties;

    // Attach refresh token cookie
    public void attachRefreshCookie(
            HttpServletResponse response,
            String token,
            long maxAgeSeconds
    ) {

        log.info("Attaching refresh token cookie, name={}",
                jwtProperties.getRefreshTokenCookieName());

        ResponseCookie.ResponseCookieBuilder builder =
                ResponseCookie.from(
                                jwtProperties.getRefreshTokenCookieName(),
                                token
                        )
                        .httpOnly(jwtProperties.isCookieHttpOnly())
                        .secure(jwtProperties.isCookieSecure())
                        .path("/")
                        .maxAge(maxAgeSeconds)
                        .sameSite(jwtProperties.getCookieSameSite());

        if (jwtProperties.getCookieDomain() != null &&
                !jwtProperties.getCookieDomain().isBlank()) {
            builder.domain(jwtProperties.getCookieDomain());
        }

        ResponseCookie cookie = builder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // Clear refresh token cookie
    public void clearRefreshCookie(HttpServletResponse response) {

        log.info("Clearing refresh token cookie");

        ResponseCookie.ResponseCookieBuilder builder =
                ResponseCookie.from(
                                jwtProperties.getRefreshTokenCookieName(),
                                ""
                        )
                        .maxAge(0)
                        .httpOnly(jwtProperties.isCookieHttpOnly())
                        .secure(jwtProperties.isCookieSecure())
                        .path("/")
                        .sameSite(jwtProperties.getCookieSameSite());

        if (jwtProperties.getCookieDomain() != null &&
                !jwtProperties.getCookieDomain().isBlank()) {
            builder.domain(jwtProperties.getCookieDomain());
        }

        ResponseCookie cookie = builder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // Prevent browser caching
    public void addNoStoreHeaders(HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setHeader("Pragma", "no-cache");
    }
}
