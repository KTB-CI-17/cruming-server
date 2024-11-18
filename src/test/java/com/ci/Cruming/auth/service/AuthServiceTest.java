package com.ci.Cruming.auth.service;

import com.ci.Cruming.auth.dto.TokenRequest;
import com.ci.Cruming.auth.dto.TokenResponse;
import com.ci.Cruming.auth.dto.UserProfile;
import com.ci.Cruming.auth.service.validator.AbstractTokenValidator;
import com.ci.Cruming.auth.service.validator.TokenValidatorFactory;
import com.ci.Cruming.common.constants.Platform;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenValidatorFactory tokenValidatorFactory;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private AbstractTokenValidator tokenValidator;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, tokenValidatorFactory, jwtTokenProvider);
    }

    @Test
    void exchangeToken_ExistingUser() {
        // given
        TokenRequest request = new TokenRequest("social_token", "NAVER");
        UserProfile profile = UserProfile.builder()
            .platformId("12345")
            .nickname("테스트유저")
            .image("http://example.com/image.jpg")
            .build();
        User existingUser = User.builder()
            .nickname("테스트유저")
            .platform(Platform.NAVER)
            .platformId(12345L)
            .build();
        TokenResponse expectedResponse = new TokenResponse("access_token", "refresh_token", null);

        when(tokenValidatorFactory.getValidator(Platform.NAVER)).thenReturn(tokenValidator);
        when(tokenValidator.validateAndGetProfile("social_token")).thenReturn(profile);
        when(userRepository.findByPlatformAndPlatformId(Platform.NAVER, 12345L))
            .thenReturn(Optional.of(existingUser));
        when(jwtTokenProvider.createToken(any(User.class))).thenReturn(expectedResponse);

        // when
        TokenResponse response = authService.exchangeToken(request);

        // then
        assertNotNull(response);
        assertEquals(expectedResponse.accessToken(), response.accessToken());
        assertEquals(expectedResponse.refreshToken(), response.refreshToken());
    }
} 