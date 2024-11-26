package com.ci.Cruming.file.dto;

import com.ci.Cruming.common.constants.FileStatus;
import com.ci.Cruming.common.constants.FileType;
import com.ci.Cruming.file.entity.File;

import java.time.LocalDateTime;

public record FileResponse(
        Long id,
        String fileName,
        String fileKey,
        String url,
        FileType fileType,
        Long fileSize,
        Integer displayOrder,
        Long userId,
        FileStatus status,
        LocalDateTime createdAt
) {
}