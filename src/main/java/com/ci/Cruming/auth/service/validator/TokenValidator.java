package com.ci.Cruming.auth.service.validator;

import org.springframework.web.client.RestTemplate;

public class TokenValidator {
    protected RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
} 