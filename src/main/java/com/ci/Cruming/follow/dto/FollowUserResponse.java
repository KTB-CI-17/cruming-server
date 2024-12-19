package com.ci.Cruming.follow.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "팔로우 유저 정보 응답 DTO")
public record FollowUserResponse(
        @Schema(description = "유저 ID")
        Long id,

        @Schema(description = "닉네임")
        String nickname,

        @Schema(description = "프로필 이미지 URL")
        String profileUrl,

        @Schema(description = "인스타그램 ID")
        String instagramId
) {
} 