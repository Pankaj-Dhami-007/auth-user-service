package com.company.auth.service;

import com.company.auth.entity.RefreshToken;
import com.company.auth.entity.UserEntity;
import com.company.auth.enums.ResponseErrorCodes;
import com.company.auth.exceptions.InvalidCredentialException;
import com.company.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService{

private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken save(UserEntity user, String token, long ttlSeconds) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiryAt(
                LocalDateTime.now().plusSeconds(ttlSeconds)
        );
        refreshToken.setIsRevoked(false);

        log.debug("Saving refresh token for userId={}", user.getId());

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken validate(String token) {

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByTokenAndIsRevokedFalse(token)
                        .orElseThrow(() -> {
                            log.warn("Refresh token not found or revoked");
                            return new InvalidCredentialException(
                                    ResponseErrorCodes.INVALID_REFRESH_TOKEN
                            );
                        });

        if (refreshToken.getExpiryAt().isBefore(LocalDateTime.now())) {
            log.warn("Refresh token expired");
            revoke(refreshToken);
            throw new InvalidCredentialException(
                    ResponseErrorCodes.INVALID_REFRESH_TOKEN
            );
        }

        return refreshToken;
    }

    @Override
    public void revoke(RefreshToken refreshToken) {

        refreshToken.setIsRevoked(true);
        refreshToken.setRevokedAt(LocalDateTime.now());

        refreshTokenRepository.save(refreshToken);

        log.debug("Refresh token revoked, id={}", refreshToken.getId());
    }

    @Override
    public void revokeAll(UserEntity user) {
        log.debug("Revoking all refresh tokens for userId={}", user.getId());
        refreshTokenRepository.deleteByUser(user);
    }
}
