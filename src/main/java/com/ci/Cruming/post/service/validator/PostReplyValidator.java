package com.ci.Cruming.post.service.validator;

import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.post.dto.PostReplyRequest;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Component
public class PostReplyValidator {
    public void validatePostReplyRequest(PostReplyRequest request) {
        if (request == null) {
            throw new CrumingException(ErrorCode.INVALID_REPLY);
        }

        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new CrumingException(ErrorCode.INVALID_REPLY);
        }

        if (request.content().getBytes(StandardCharsets.UTF_8).length > 500 * 3) {
            throw new CrumingException(ErrorCode.INVALID_REPLY_SIZE);
        }
    }
}