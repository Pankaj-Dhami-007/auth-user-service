package com.company.auth.security.jwt;

import com.company.auth.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@RequiredArgsConstructor
public class JwtTokenValidator {

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String token) {
        try {
            Jwts
                    .parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("typ") == null; // access token has no "typ"
    }

    public boolean isRefreshToken(String token) {
        Claims claims = parseClaims(token);
        return "refresh".equals(claims.get("typ", String.class));
    }

    public Claims parseClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    // ✅ FOR REFRESH TOKEN
    public Long getUserIdFromRefreshToken(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    // ✅ FOR ACCESS TOKEN
    public Long getUserIdFromAccessToken(String token) {
        return parseClaims(token).get("uid", Long.class);
    }

    public Long getClientIdFromAccessToken(String token) {
        return parseClaims(token).get("cid", Long.class);
    }

    public String getJti(String token) {
        return parseClaims(token).getId();
    }
}
