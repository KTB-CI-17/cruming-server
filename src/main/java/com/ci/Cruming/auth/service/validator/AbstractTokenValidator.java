package com.ci.Cruming.auth.service.validator;

import com.ci.Cruming.auth.dto.UserProfile;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public abstract class AbstractTokenValidator {
    private final RestTemplate restTemplate = new RestTemplate();

    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }

    // 템플릿 메서드
    public final UserProfile validateAndGetProfile(String accessToken) {
        HttpHeaders headers = createHeaders(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = getRestTemplate().exchange(
                    getProfileUrl(),
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return extractProfile(response.getBody());
            }
            throw new IllegalArgumentException("Failed to validate token");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token", e);
        }
    }

    // 하위 클래스에서 구현할 추상 메서드들
    protected abstract String getProfileUrl();
    protected abstract HttpHeaders createHeaders(String accessToken);
    protected abstract UserProfile extractProfile(String responseBody);
} 