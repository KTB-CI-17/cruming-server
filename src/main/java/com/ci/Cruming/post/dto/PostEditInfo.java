package com.ci.Cruming.post.dto;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.file.dto.FileResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record PostEditInfo(
        Long id,
        Category category,
        String title,
        String content,
        String location,
        String level,
        List<FileResponse> files
) {
}
