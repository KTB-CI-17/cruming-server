package com.ci.Cruming.auth.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SocialTokenValidator {
    private final RestTemplate restTemplate = new RestTemplate();

    // 템플릿 메서드
    public boolean validateToken(String accessToken, String tokenInfoUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    tokenInfoUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            // 토큰이 유효하지 않거나 예외가 발생한 경우 false 반환
            return false;
        }
    }
}
