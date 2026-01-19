package com.company.auth.filter;

import com.company.auth.contextHolder.ClientContextHolder;
import com.company.auth.entity.Client;
import com.company.auth.repository.ClientRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.company.auth.enums.ResponseErrorCodes.INVALID_CLIENT_FOUND;

@Component
@Slf4j
@Order(1)
@RequiredArgsConstructor
public class ClientValidationFilter extends OncePerRequestFilter {

    private static final String CLIENT_ID_HEADER = "X-Client-Id";
    private final ClientRepository clientRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String clientIdHeader = request.getHeader(CLIENT_ID_HEADER);
        log.info("Received request: [{} {}], clientId: {}",
                request.getMethod(), request.getRequestURI(), clientIdHeader);

        if (clientIdHeader == null || clientIdHeader.isBlank()) {
            log.warn("Missing X-Client-Id header");
            response.sendError(
                    HttpStatus.BAD_REQUEST.value(),
                    "X-Client-Id header is required"
            );
            return;
        }

        Long clientId;
        try {
            clientId = Long.parseLong(clientIdHeader);
        } catch (NumberFormatException e) {
            log.warn("Invalid X-Client-Id header value: {}", clientIdHeader);
            response.sendError(
                    HttpStatus.BAD_REQUEST.value(),
                    "Invalid X-Client-Id header"
            );
            return;
        }

        try {

            Client client = clientRepository
                    .findByIdAndIsActiveTrue(clientId)
                    .orElse(null);

            if (client == null) {
                log.warn("Invalid or inactive clientId={}", clientId);
                response.sendError(
                        HttpStatus.UNAUTHORIZED.value(),
                        INVALID_CLIENT_FOUND.getErrorMsg()
                );
                return;
            }

            ClientContextHolder.setClientId(clientId);
            log.debug("Client validated successfully: id={}, code={}",
                    client.getId(), client.getCode());

            filterChain.doFilter(request, response);

        } finally {
            // very important to avoid memory leaks
            ClientContextHolder.clear();
        }
    }

    /**
     * Skip filter for endpoints where clientId is not required
     */
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        String path = request.getRequestURI();
//
//        return path.startsWith("/auth/user/api/v1/login")
//                || path.startsWith("/auth/api/v1/refresh")
//                || path.startsWith("/auth/api/v1/logout");
//    }
}
