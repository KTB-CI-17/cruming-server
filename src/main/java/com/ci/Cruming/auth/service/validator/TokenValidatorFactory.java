package com.ci.Cruming.auth.service.validator;

import com.ci.Cruming.common.constants.Platform;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TokenValidatorFactory {
    private final Map<Platform, AbstractTokenValidator> validators;

    public TokenValidatorFactory(NaverTokenValidator naverValidator, 
                               KakaoTokenValidator kakaoValidator) {
        this.validators = Map.of(
            Platform.NAVER, naverValidator,
            Platform.KAKAO, kakaoValidator
        );
    }

    public AbstractTokenValidator getValidator(Platform platform) {
        AbstractTokenValidator validator = validators.get(platform);
        if (validator == null) {
            throw new IllegalArgumentException("Unsupported platform: " + platform);
        }
        return validator;
    }
} 