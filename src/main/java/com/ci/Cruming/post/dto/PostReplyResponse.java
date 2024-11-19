package com.ci.Cruming.post.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostReplyResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        String userProfile,
        String userNickname,
        List<PostReplyResponse> children
) {
}
