package com.ci.Cruming.post.dto;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.common.constants.Visibility;
import com.ci.Cruming.file.dto.FileResponse;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        Long id,
        String title,
        String content,
        String location,
        String level,
        Category category,
        Visibility visibility,
        LocalDateTime createdAt,
        Long userId,
        String userNickname,
        String userProfile,
        String instagram_id,
        boolean isWriter,
        List<FileResponse> files,
        boolean isLiked,
        Long likeCount,
        Long replyCount,
        Long views
) {

}

