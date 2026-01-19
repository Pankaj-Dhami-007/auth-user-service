package com.company.auth.service;

import com.company.auth.entity.RefreshToken;
import com.company.auth.entity.UserEntity;

public interface RefreshTokenService {

    RefreshToken save(UserEntity user, String refreshToken, long ttlSeconds);

    RefreshToken validate(String refreshToken);

    void revoke(RefreshToken refreshToken);

    void revokeAll(UserEntity user);
}
