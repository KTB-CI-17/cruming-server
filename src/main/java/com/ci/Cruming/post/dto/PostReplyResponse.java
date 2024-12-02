package com.ci.Cruming.post.dto;

import java.time.LocalDateTime;

public record PostReplyResponse(
        Long id,
        Long userId,
        String content,
        LocalDateTime createdAt,
        String userProfile,
        String userNickname,
        boolean isWriter,
        Long childCount
) {
}
