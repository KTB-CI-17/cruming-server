package com.ci.Cruming.post.dto;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.file.dto.FileRequest;
import com.ci.Cruming.location.dto.LocationRequest;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "게시글 생성 요청 DTO")
public record PostRequest(
        @Schema(description = "게시글 카테고리",
                example = "PROBLEM")
        Category category,

        @Schema(description = "게시글 제목",
                example = "새로 나온 문제입니다",
                maxLength = 100)
        String title,

        @Schema(description = "게시글 내용",
                example = "시작 홀드는 파란색이고 완등 홀드는 노란색입니다.")
        String content,

        @Schema(description = "암장 위치 정보")
        LocationRequest locationRequest,

        @Schema(description = "문제 난이도",
                example = "V3",
                maxLength = 50)
        String level,

        @Schema(description = "첨부 파일 목록")
        List<FileRequest> fileRequests
) {
}