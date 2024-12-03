package com.ci.Cruming.post.service.validator;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.file.dto.FileRequest;
import com.ci.Cruming.post.dto.PostRequest;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.user.entity.User;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class PostValidator {

    private static final int MAX_TITLE_BYTES = 100 * 3;
    private static final int MAX_CONTENT_BYTES = 1000 * 3;
    private static final int MAX_LEVEL_BYTES = 50 * 3;
    private static final int MAX_PROBLEM_FILE_SIZE = 2;

    public void validatePostRequest(PostRequest request) {
        nullCheck(request);
        validateTitle(request.title());
        validateContent(request.content());

        if (Category.isProblem(request.category())) {
            validateLevel(request.level());
            validateFileRequestSize(request.fileRequests());
            nullCheck(request.locationRequest());
        }
    }

    public void validatePostAuthor(Post post, User user) {
        if (!post.getUser().equals(user)) {
            throw new CrumingException(ErrorCode.POST_NOT_AUTHORIZED);
        }
    }

    private void nullCheck(Object object) {
        if (object == null) {
            throw new CrumingException(ErrorCode.INVALID_REQUEST);
        }
    }

    private void validateFileRequestSize(List<FileRequest> fileRequests) {
        if (fileRequests == null || fileRequests.isEmpty()) {
            throw new CrumingException(ErrorCode.EMPTY_FILE);
        }

        if (fileRequests.size() > MAX_PROBLEM_FILE_SIZE) {
            throw new CrumingException(ErrorCode.PROBLEM_FILE_SIZE_OVER);
        }
    }

    private void nullOrEmptyCheck(String value, ErrorCode errorCode) {
        if (value == null || value.trim().isEmpty()) {
            throw new CrumingException(errorCode);
        }
    }

    private void validateTitle(String title) {
        nullOrEmptyCheck(title, ErrorCode.INVALID_TITLE);
        byteSizeCheck(title, MAX_TITLE_BYTES, ErrorCode.INVALID_POST_TITLE_SIZE);
    }

    private void validateContent(String content) {
        nullOrEmptyCheck(content, ErrorCode.INVALID_CONTENT);
        byteSizeCheck(content, MAX_CONTENT_BYTES, ErrorCode.INVALID_POST_CONTENT_SIZE);
    }

    private void validateLevel(String level) {
        nullOrEmptyCheck(level, ErrorCode.INVALID_LEVEL);
        byteSizeCheck(level, MAX_LEVEL_BYTES, ErrorCode.INVALID_POST_LEVEL_SIZE);
    }

    private void byteSizeCheck(String value, int maxBytes, ErrorCode errorCode) {
        if (value.getBytes(StandardCharsets.UTF_8).length > maxBytes) {
            throw new CrumingException(errorCode);
        }
    }
}