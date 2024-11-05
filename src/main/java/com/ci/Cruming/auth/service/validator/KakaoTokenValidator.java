package com.ci.Cruming.auth.service.validator;

import com.ci.Cruming.auth.dto.UserProfile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class KakaoTokenValidator extends AbstractTokenValidator {
    private static final String PROFILE_URL = "https://kapi.kakao.com/v2/user/me";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected String getProfileUrl() {
        return PROFILE_URL;
    }

    @Override
    protected HttpHeaders createHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }

    @Override
    protected UserProfile extractProfile(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode kakaoAccount = root.get("kakao_account");
            JsonNode profile = kakaoAccount.get("profile");
            
            return UserProfile.builder()
                    .platformId(root.get("id").asText())
                    .nickname(profile.get("nickname").asText())
                    .image(profile.get("profile_image_url").asText())
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse Kakao profile", e);
        }
    }
}