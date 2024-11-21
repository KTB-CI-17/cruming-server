package com.ci.Cruming.post.dto;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.common.constants.Visibility;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String title,
        String content,
        String Location,
        String level,
        Category category,
        Visibility visibility,
        LocalDateTime createdAt,
        Long userId,
        String userNickname,
        boolean isWriter
) {

}

