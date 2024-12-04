package com.ci.Cruming.file.dto;

import com.ci.Cruming.common.constants.FileStatus;
import com.ci.Cruming.common.constants.FileType;
import com.ci.Cruming.file.entity.File;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

// TODO: S3 연동 후 리팩토링 필요
@Schema(description = "파일 정보 응답 DTO")
public record FileResponse(
        @Schema(description = "파일 ID")
        Long id,

        @Schema(description = "파일 이름")
        String fileName,

        @Schema(description = "파일 저장 키")
        String fileKey,

        @Schema(description = "파일 접근 URL")
        String url,

        @Schema(description = "파일 타입")
        FileType fileType,

        @Schema(description = "파일 크기 (byte)")
        Long fileSize,

        @Schema(description = "파일 표시 순서")
        Integer displayOrder,

        @Schema(description = "파일 업로더 ID")
        Long userId,

        @Schema(description = "파일 상태")
        FileStatus status,

        @Schema(description = "파일 생성 일시")
        LocalDateTime createdAt
) {
}