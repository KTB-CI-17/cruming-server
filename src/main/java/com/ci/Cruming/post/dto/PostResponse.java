package com.ci.Cruming.post.dto;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.common.constants.Visibility;
import com.ci.Cruming.file.dto.FileResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "게시글 상세 조회 응답 DTO")
public record PostResponse(
        @Schema(description = "게시글 ID")
        Long id,

        @Schema(description = "제목")
        String title,

        @Schema(description = "내용")
        String content,

        @Schema(description = "암장 위치")
        String location,

        @Schema(description = "난이도")
        String level,

        @Schema(description = "게시글 카테고리")
        Category category,

        @Schema(description = "게시글 공개 여부")
        Visibility visibility,

        @Schema(description = "작성일시")
        LocalDateTime createdAt,

        @Schema(description = "작성자 ID")
        Long userId,

        @Schema(description = "작성자 닉네임")
        String userNickname,

        @Schema(description = "작성자 프로필 이미지 URL")
        String userProfile,

        @Schema(description = "작성자 인스타그램 ID")
        String instagram_id,

        @Schema(description = "현재 사용자가 작성자인지 여부")
        boolean isWriter,

        @Schema(description = "첨부 파일 목록")
        List<FileResponse> files,

        @Schema(description = "현재 사용자의 좋아요 여부")
        boolean isLiked,

        @Schema(description = "좋아요 수")
        Long likeCount,

        @Schema(description = "댓글 수")
        Long replyCount,

        @Schema(description = "조회수")
        Long views
) {
}