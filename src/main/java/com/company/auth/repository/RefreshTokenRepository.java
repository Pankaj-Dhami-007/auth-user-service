package com.company.auth.repository;

import com.company.auth.entity.RefreshToken;
import com.company.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByTokenAndIsRevokedFalse(String token);
    void deleteByUserId(Long userId);

    void deleteByUser(UserEntity user);
}