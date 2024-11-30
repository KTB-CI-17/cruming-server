package com.ci.Cruming.auth.service;

import com.ci.Cruming.auth.dto.TokenResponse;
import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.user.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final SecretKey secretKey;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private final RefreshTokenService refreshTokenService;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-in-seconds:3600}") long accessTokenValidityInSeconds,
            @Value("${jwt.refresh-token-validity-in-seconds:86400}") long refreshTokenValidityInSeconds,
            RefreshTokenService refreshTokenService
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000;
        this.refreshTokenService = refreshTokenService;
    }

    public TokenResponse createToken(User user) {
        Date now = new Date();
        Date accessTokenValidity = new Date(now.getTime() + accessTokenValidityInMilliseconds);
        Date refreshTokenValidity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);
        LocalDateTime refreshTokenExpiryDate = LocalDateTime.ofInstant(refreshTokenValidity.toInstant(),
                ZoneId.systemDefault());

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("userId", user.getId())
                .claim("platform", user.getPlatform().name())
                .setIssuedAt(now)
                .setExpiration(accessTokenValidity)
                .signWith(secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(now)
                .setExpiration(refreshTokenValidity)
                .signWith(secretKey)
                .compact();

        refreshTokenService.saveRefreshToken(user, refreshToken, refreshTokenExpiryDate);

        return new TokenResponse(
                accessToken,
                refreshToken,
                LocalDateTime.ofInstant(accessTokenValidity.toInstant(), ZoneId.systemDefault())
        );
    }

    public Long getUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        if (!validateToken(token)) {
            return false;
        }

        return refreshTokenService.findByToken(token)
                .map(refreshToken -> {
                    try {
                        refreshTokenService.verifyExpiration(refreshToken);
                        return true;
                    } catch (CrumingException e) {
                        return false;
                    }
                })
                .orElse(false);
    }
}