package com.ci.Cruming.post.dto;

import java.time.LocalDateTime;

public record PostListResponse(
        Long id,
        String title,
        LocalDateTime createdAt
) {

    public static PostListResponse from(PostDTO dto) {
        return new PostListResponse(dto.id(), dto.title(), dto.createdAt());
    }
}
