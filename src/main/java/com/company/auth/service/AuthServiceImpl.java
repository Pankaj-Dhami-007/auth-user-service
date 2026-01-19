package com.company.auth.service;

import com.company.auth.contextHolder.ClientContextHolder;
import com.company.auth.dtos.LoginRequest;
import com.company.auth.dtos.LoginResponse;
import com.company.auth.entity.RefreshToken;
import com.company.auth.entity.UserEntity;
import com.company.auth.enums.ResponseErrorCodes;
import com.company.auth.exceptions.InvalidCredentialException;
import com.company.auth.exceptions.ResourceNotFoundException;
import com.company.auth.repository.UserRepository;
import com.company.auth.security.CookieService;
import com.company.auth.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static com.company.auth.enums.ResponseErrorCodes.USER_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final CookieService cookieService;

    @Override
    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {

        Long clientId = ClientContextHolder.getClientId();
        log.debug("Login attempt for username={}, clientId: {}", loginRequest.getUsername(), clientId);

        UserEntity user = userRepository
                .findByUsernameAndClientId(
                        loginRequest.getUsername(),
                        clientId
                )
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
        Authentication authentication = this.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

//        UserEntity user =
//                ((CustomUserDetails) authentication.getPrincipal()).getUser();

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        refreshTokenService.save(
                user,
                refreshToken,
                jwtTokenProvider.getRefreshTokenTtlSeconds()
        );

        cookieService.attachRefreshCookie(response,refreshToken,jwtTokenProvider.getRefreshTokenTtlSeconds());
        cookieService.addNoStoreHeaders(response);

        return new LoginResponse(accessToken, jwtTokenProvider.getAccessTokenTtlSeconds());
    }

    private Authentication authenticate(String username, String password){
        try {
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        }
        catch (BadCredentialsException ex){
            log.warn("Invalid credentials provided for username: {}", username);
            throw new InvalidCredentialException(ResponseErrorCodes.INVALID_CREDENTIALS);
        }
    }

    @Override
    public LoginResponse refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        log.info("Refresh token request received");

        String oldRefreshToken =
                (String) request.getAttribute("refreshToken");

        if (oldRefreshToken == null) {
            log.warn("Refresh token missing in request");
            throw new InvalidCredentialException(
                    ResponseErrorCodes.INVALID_REFRESH_TOKEN
            );
        }
        RefreshToken storedToken =
                refreshTokenService.validate(oldRefreshToken);

        UserEntity user = storedToken.getUser();

        log.debug("Refresh token validated for userId={}", user.getId());

        refreshTokenService.revoke(storedToken);
        log.debug("Old refresh token revoked, tokenId={}", storedToken.getId());

        String newAccessToken =
                jwtTokenProvider.generateAccessToken(user);

        String newRefreshToken =
                jwtTokenProvider.generateRefreshToken(user);

        refreshTokenService.save(
                user,
                newRefreshToken,
                jwtTokenProvider.getRefreshTokenTtlSeconds()
        );

        log.debug("New refresh token issued for userId={}", user.getId());

        cookieService.attachRefreshCookie(
                response,
                newRefreshToken,
                (int) jwtTokenProvider.getRefreshTokenTtlSeconds()
        );

        cookieService.addNoStoreHeaders(response);

        log.info("Refresh token flow completed successfully for userId={}", user.getId());

        return new LoginResponse(
                newAccessToken,
                jwtTokenProvider.getAccessTokenTtlSeconds()
        );
    }
}
