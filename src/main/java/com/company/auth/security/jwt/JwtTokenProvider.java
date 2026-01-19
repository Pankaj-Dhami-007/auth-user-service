package com.company.auth.security.jwt;

import com.company.auth.config.JwtProperties;
import com.company.auth.entity.RoleEntity;
import com.company.auth.entity.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(UserEntity user) {

        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofMinutes(jwtProperties.getAccessTokenExpiryMinutes()));

        List<String> roles = user.getRoles()
                .stream()
                .map(RoleEntity::getRoleName)
                .toList();

        return Jwts.builder()
                .setSubject(user.getUsername())          // primary identity
                .claim("uid", user.getId())              // internal user id
                .claim("cid", user.getClientId())  // client (tenant)
                .claim("roles", roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserEntity user) {

        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofDays(jwtProperties.getRefreshTokenExpiryDays()));

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())     // jti (for revoke)
                .setSubject(user.getId().toString())     // user id as subject
                .claim("typ", "refresh")                 // differentiate
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public long getAccessTokenTtlSeconds() {
        return Duration.ofMinutes(jwtProperties.getAccessTokenExpiryMinutes()).getSeconds();
    }

    public long getRefreshTokenTtlSeconds() {
        return Duration.ofDays(jwtProperties.getRefreshTokenExpiryDays()).getSeconds();
    }
}
