package com.ci.Cruming.post.service.validator;

import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.post.dto.PostGeneralRequest;
import com.ci.Cruming.post.dto.PostProblemRequest;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.user.entity.User;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class PostValidator {

    private static final int MAX_TITLE_BYTES = 100 * 3;
    private static final int MAX_CONTENT_BYTES = 1000 * 3;
    private static final int MAX_LEVEL_BYTES = 50 * 3;

    public void validatePostGeneralRequest(PostGeneralRequest request) {
        if (request == null) {
            throw new CrumingException(ErrorCode.INVALID_REQUEST);
        }

        if (request.title() == null || request.title().trim().isEmpty()) {
            throw new CrumingException(ErrorCode.INVALID_TITLE);
        }

        if (request.title().getBytes(StandardCharsets.UTF_8).length > MAX_TITLE_BYTES) {
            throw new CrumingException(ErrorCode.INVALID_POST_TITLE_SIZE);
        }

        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new CrumingException(ErrorCode.INVALID_CONTENT);
        }

        if (request.content().getBytes(StandardCharsets.UTF_8).length > MAX_CONTENT_BYTES) {
            throw new CrumingException(ErrorCode.INVALID_POST_CONTENT_SIZE);
        }
    }

    public void validatePostProblemRequest(PostProblemRequest request) {
        if (request == null) {
            throw new CrumingException(ErrorCode.INVALID_REQUEST);
        }

        if (request.title() == null || request.title().trim().isEmpty()) {
            throw new CrumingException(ErrorCode.INVALID_TITLE);
        }

        if (request.title().getBytes(StandardCharsets.UTF_8).length > MAX_TITLE_BYTES) {
            throw new CrumingException(ErrorCode.INVALID_POST_TITLE_SIZE);
        }

        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new CrumingException(ErrorCode.INVALID_CONTENT);
        }

        if (request.content().getBytes(StandardCharsets.UTF_8).length > MAX_CONTENT_BYTES) {
            throw new CrumingException(ErrorCode.INVALID_POST_CONTENT_SIZE);
        }

        if (request.level() == null || request.level().trim().isEmpty()) {
            throw new CrumingException(ErrorCode.INVALID_LEVEL);
        }

        if (request.level().getBytes(StandardCharsets.UTF_8).length > MAX_LEVEL_BYTES) {
            throw new CrumingException(ErrorCode.INVALID_POST_LEVEL_SIZE);
        }

        if (request.location() == null) {
            throw new CrumingException(ErrorCode.INVALID_LOCATION);
        }
    }

    public void validatePostAuthor(Post post, User user) {
        if (!post.getUser().equals(user)) {
            throw new CrumingException(ErrorCode.POST_NOT_AUTHORIZED);
        }
    }
}