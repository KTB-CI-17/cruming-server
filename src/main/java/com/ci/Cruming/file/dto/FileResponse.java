package com.ci.Cruming.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "파일 정보 응답 DTO")
public record FileResponse(
        @Schema(description = "파일 ID")
        Long id,
        @Schema(description = "파일 이름")
        String fileName,
        @Schema(description = "파일 접근 URL")
        String url,
        @Schema(description = "파일 표시 순서")
        Integer displayOrder
) {
}