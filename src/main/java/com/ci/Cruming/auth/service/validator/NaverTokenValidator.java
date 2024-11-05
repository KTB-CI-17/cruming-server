package com.ci.Cruming.auth.service.validator;

import com.ci.Cruming.auth.dto.UserProfile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class NaverTokenValidator extends AbstractTokenValidator {
    private static final String PROFILE_URL = "https://openapi.naver.com/v1/nid/me";
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
            JsonNode response = root.get("response");
            
            return UserProfile.builder()
                    .platformId(response.get("id").asText())
                    .nickname(response.get("nickname").asText())
                    .image(response.get("profile_image").asText())
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse Naver profile", e);
        }
    }
} 