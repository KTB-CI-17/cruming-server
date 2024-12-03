package com.ci.Cruming.post.dto;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.file.dto.FileRequest;
import com.ci.Cruming.location.dto.LocationRequest;

import java.util.List;

public record PostEditRequest(
        Category category,
        String title,
        String content,
        LocationRequest locationRequest,
        String level,
        List<FileRequest> newFiles,
        List<Long> deleteFileIds
) {
}
