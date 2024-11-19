package com.ci.Cruming.post.service.validator;

import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.post.dto.PostReplyRequest;
import com.ci.Cruming.post.entity.PostReply;
import com.ci.Cruming.user.entity.User;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Component
public class PostReplyValidator {

    private static final int MAX_CONTENT_BYTES = 1000 * 3;

    public void validatePostReplyRequest(PostReplyRequest request) {
        if (request == null) {
            throw new CrumingException(ErrorCode.INVALID_REPLY);
        }

        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new CrumingException(ErrorCode.INVALID_REPLY);
        }

        if (request.content().getBytes(StandardCharsets.UTF_8).length > MAX_CONTENT_BYTES) {
            throw new CrumingException(ErrorCode.INVALID_REPLY_SIZE);
        }
    }

    public void validatePostReplyAuthor(PostReply postReply, User user) {
        if (!postReply.getUser().equals(user)) {
            throw new CrumingException(ErrorCode.POST_REPLY_NOT_AUTHORIZED);
        }
    }
}
