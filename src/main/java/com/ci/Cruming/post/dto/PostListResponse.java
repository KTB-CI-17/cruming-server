package com.ci.Cruming.post.dto;

import java.time.LocalDateTime;

public record PostListResponse(
        Long id,
        String title,
        LocalDateTime createdAt
) {
}
