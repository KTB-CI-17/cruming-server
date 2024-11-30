package com.ci.Cruming.auth.service;

import com.ci.Cruming.auth.dto.TokenRequest;
import com.ci.Cruming.auth.dto.TokenResponse;
import com.ci.Cruming.auth.dto.UserProfile;
import com.ci.Cruming.common.constants.Platform;
import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.auth.entity.KakaoTokenResponse;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.user.repository.UserRepository;
import com.ci.Cruming.auth.service.validator.AbstractTokenValidator;
import com.ci.Cruming.auth.service.validator.TokenValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final TokenValidatorFactory tokenValidatorFactory;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${kakao.auth.client-id}")
    private String clientId;

    @Value("${kakao.auth.client-secret}")
    private String clientSecret;

    @Value("${kakao.auth.redirect-uri}")
    private String redirectUri;

    public TokenRequest kakaoLoginAPI(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        headers.set("charset", "utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("client_secret", clientSecret);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<KakaoTokenResponse> response = restTemplate.exchange(
                    "https://kauth.kakao.com/oauth/token",
                    HttpMethod.POST,
                    requestEntity,
                    KakaoTokenResponse.class
            );
            return new TokenRequest(Objects.requireNonNull(response.getBody()).accessToken(), "KAKAO");
        } catch (Exception e) {
            throw new CrumingException(ErrorCode.FAIL_GET_KAKAO_ACCESS_TOKEN, e.getMessage());
        }
    }

    public TokenResponse exchangeToken(TokenRequest request) {
        Platform platform = Platform.valueOf(request.provider().toUpperCase());
        AbstractTokenValidator validator = tokenValidatorFactory.getValidator(platform);
        
        UserProfile profile = validator.validateAndGetProfile(request.socialToken());
        System.out.println("profile = " + profile);
        User user = userRepository.findByPlatformAndPlatformId(
                platform,
                profile.getPlatformId()
            )
            .orElseGet(() -> createUser(profile, platform));

        return jwtTokenProvider.createToken(user);
    }

    private User createUser(UserProfile profile, Platform platform) {
        User user = User.builder()
            .nickname(profile.getNickname())
            .platform(platform)
            .platformId(profile.getPlatformId())
            .build();
        System.out.println("user = " + user);
        return userRepository.save(user);
    }

    public TokenResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        
        Long userId = jwtTokenProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return jwtTokenProvider.createToken(user);
    }
} 