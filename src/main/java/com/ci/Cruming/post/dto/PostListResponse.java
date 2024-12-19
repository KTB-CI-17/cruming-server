package com.ci.Cruming.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "게시글 목록 조회 응답 DTO")
public record PostListResponse(
        @Schema(description = "게시글 ID",
                example = "1")
        Long id,

        @Schema(description = "게시글 제목",
                example = "V3 새로 나온 문제입니다")
        String title,

        @Schema(description = "게시글 생성 일시",
                example = "2024-01-01T13:30:00")
        LocalDateTime createdAt
) {
}