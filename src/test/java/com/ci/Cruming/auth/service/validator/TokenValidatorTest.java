package com.ci.Cruming.auth.service.validator;

import com.ci.Cruming.auth.dto.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TokenValidatorTest {
    private RestTemplate mockRestTemplate;
    private NaverTokenValidator naverValidator;
    private KakaoTokenValidator kakaoValidator;

    @BeforeEach
    void setUp() {
        mockRestTemplate = mock(RestTemplate.class);
        naverValidator = new NaverTokenValidator() {
            @Override
            protected RestTemplate getRestTemplate() {
                return mockRestTemplate;
            }
        };
        kakaoValidator = new KakaoTokenValidator() {
            @Override
            protected RestTemplate getRestTemplate() {
                return mockRestTemplate;
            }
        };
    }

    @Test
    void validateNaverToken_Success() {
        // given
        String mockResponse = """
            {
                "resultcode": "00",
                "message": "success",
                "response": {
                    "id": "12345",
                    "nickname": "홍길동",
                    "profile_image": "http://example.com/image.jpg"
                }
            }
            """;
        when(mockRestTemplate.exchange(
            eq("https://openapi.naver.com/v1/nid/me"),
            any(), any(), eq(String.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        // when
        UserProfile profile = naverValidator.validateAndGetProfile("valid_token");

        // then
        assertEquals("12345", profile.getPlatformId());
        assertEquals("홍길동", profile.getNickname());
        assertEquals("http://example.com/image.jpg", profile.getImage());
    }

    @Test
    void validateKakaoToken_Success() {
        // given
        String mockResponse = """
            {
                "id": 12345,
                "kakao_account": {
                    "profile": {
                        "nickname": "홍길동",
                        "profile_image_url": "http://example.com/image.jpg"
                    }
                }
            }
            """;
        when(mockRestTemplate.exchange(
            eq("https://kapi.kakao.com/v2/user/me"),
            any(), any(), eq(String.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        // when
        UserProfile profile = kakaoValidator.validateAndGetProfile("valid_token");

        // then
        assertEquals("12345", profile.getPlatformId());
        assertEquals("홍길동", profile.getNickname());
        assertEquals("http://example.com/image.jpg", profile.getImage());
    }

    @Test
    void validateToken_InvalidToken() {
        // given
        when(mockRestTemplate.exchange(
            any(), any(), any(), eq(String.class)
        )).thenThrow(new RuntimeException("Invalid token"));

        // then
        assertThrows(IllegalArgumentException.class, () -> 
            naverValidator.validateAndGetProfile("invalid_token")
        );
    }
} 