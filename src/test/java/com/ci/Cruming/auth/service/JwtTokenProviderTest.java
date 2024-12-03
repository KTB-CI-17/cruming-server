package com.ci.Cruming.auth.service;

import com.ci.Cruming.auth.dto.TokenResponse;
import com.ci.Cruming.common.constants.Platform;
import com.ci.Cruming.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {
    private JwtTokenProvider jwtTokenProvider;
    private static final String SECRET_KEY = "your-256-bit-secret-key-here-must-be-at-least-32-characters";

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(SECRET_KEY, 3600, 86400);
    }

    @Test
    void createToken_Success() {
        // given
        User user = User.builder()
            .nickname("테스트유저")
            .platform(Platform.NAVER)
            .platformId(String.valueOf(12345L))
            .build();

        // when
        TokenResponse tokenResponse = jwtTokenProvider.createToken(user);

        // then
        assertNotNull(tokenResponse.accessToken());
        assertNotNull(tokenResponse.refreshToken());
        assertNotNull(tokenResponse.expiresAt());
    }

    @Test
    void validateToken_Success() {
        // given
        User user = User.builder()
            .nickname("테스트유저")
            .platform(Platform.NAVER)
            .platformId(String.valueOf(12345L))
            .build();
        TokenResponse tokenResponse = jwtTokenProvider.createToken(user);

        // when
        boolean isValid = jwtTokenProvider.validateToken(tokenResponse.accessToken());

        // then
        assertTrue(isValid);
    }

    @Test
    void getUserId_Success() {
        // given
        User user = User.builder()
            .nickname("테스트유저")
            .platform(Platform.NAVER)
            .platformId(String.valueOf(12345L))
            .build();
        TokenResponse tokenResponse = jwtTokenProvider.createToken(user);

        // when
        Long userId = jwtTokenProvider.getUserId(tokenResponse.accessToken());

        // then
        assertEquals(user.getId(), userId);
    }

    @Test
    void validateToken_InvalidToken() {
        // given
        String invalidToken = "invalid.token.string";

        // when & then
        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }
} 