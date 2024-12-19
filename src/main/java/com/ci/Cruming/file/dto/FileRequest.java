package com.ci.Cruming.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "파일 업로드 요청 DTO")
public record FileRequest(
        @Schema(description = "원본 파일명",
                example = "climbing_problem.jpg")
        String originalFileName,

        @Schema(description = "파일 표시 순서",
                example = "1")
        Integer displayOrder
) {
}