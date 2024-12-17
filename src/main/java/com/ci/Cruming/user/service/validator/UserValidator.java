package com.ci.Cruming.user.service.validator;

import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.user.dto.UserEditRequest;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class UserValidator {

    private static final int MAX_NICKNAME_BYTES = 50 * 3;
    private static final int MAX_INTRO_BYTES = 300 * 3;
    private static final int MAX_INSTAGRAM_ID_BYTES = 300 * 3;

    public void validateUserEditRequest(UserEditRequest request) {
        nullCheck(request);
        validateNickname(request.nickname());
        validateHeight(request.height());
        validateIntro(request.intro());
        validateInstagramId(request.instagramId());
    }

    private void validateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new CrumingException(ErrorCode.INVALID_USER_NICKNAME);
        }
        if (nickname.getBytes(StandardCharsets.UTF_8).length > MAX_NICKNAME_BYTES) {
            throw new CrumingException(ErrorCode.INVALID_USER_NICKNAME);
        }
    }

    private void validateHeight(Short height) {
        if (height == null) {
            return;
        }

        if (height <= 30 || height >= 300) {
            throw new CrumingException(ErrorCode.INVALID_USER_HEIGHT_SIZE);
        }
    }

    private void validateIntro(String intro) {
        if (intro == null) {
            return;
        }

        if (intro.length() > MAX_INTRO_BYTES) {
            throw new CrumingException(ErrorCode.INVALID_USER_INTRO_SIZE);
        }
    }

    private void validateInstagramId(String instagramId) {
        if (instagramId == null) {
            return;
        }

        if (instagramId.length() > MAX_INSTAGRAM_ID_BYTES) {
            throw new CrumingException(ErrorCode.INVALID_USER_INSTAGRAM_ID_SIZE);
        }
    }


    private void nullCheck(Object object) {
        if (object == null) {
            throw new CrumingException(ErrorCode.INVALID_REQUEST);
        }
    }
}
