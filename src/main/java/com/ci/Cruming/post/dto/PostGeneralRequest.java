package com.ci.Cruming.post.dto;

import com.ci.Cruming.file.dto.FileRequest;

import java.util.List;

public record PostGeneralRequest(
        String title,
        String content,
        List<FileRequest> files
        ) {
}
