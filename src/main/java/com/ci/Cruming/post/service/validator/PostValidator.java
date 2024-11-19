package com.ci.Cruming.post.service.validator;

import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.post.dto.PostGeneralRequest;
import com.ci.Cruming.post.dto.PostProblemRequest;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PostValidator {

    public void validatePostAuthor(Post post, User user) {
        if (!post.getUser().equals(user)) {
            throw new CrumingException(ErrorCode.POST_NOT_AUTHORIZED);
        }
    }

    public void validatePostGeneralRequest(PostGeneralRequest request) {
        if (request == null) {
            throw new CrumingException(ErrorCode.INVALID_REQUEST);
        }

        if (request.title() == null || request.title().trim().isEmpty()) {
            throw new CrumingException(ErrorCode.INVALID_TITLE);
        }

        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new CrumingException(ErrorCode.INVALID_CONTENT);
        }
    }

    public void validatePostProblemRequest(PostProblemRequest request) {
        if (request == null) {
            throw new CrumingException(ErrorCode.INVALID_REQUEST);
        }

        if (request.title() == null || request.title().trim().isEmpty()) {
            throw new CrumingException(ErrorCode.INVALID_TITLE);
        }

        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new CrumingException(ErrorCode.INVALID_CONTENT);
        }

        if (request.level() == null || request.level().trim().isEmpty()) {
            throw new CrumingException(ErrorCode.INVALID_LEVEL);
        }

        if (request.location() == null) {
            throw new CrumingException(ErrorCode.INVALID_LOCATION);
        }
    }

}
