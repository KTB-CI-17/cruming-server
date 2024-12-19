package com.ci.Cruming.timeline.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ci.Cruming.common.constants.Visibility;
import com.ci.Cruming.file.dto.FileResponse;
import com.ci.Cruming.timeline.entity.Timeline;
import com.ci.Cruming.user.entity.User;

import lombok.Builder;
import java.time.LocalDate;


@Builder
public record TimelineResponse(
    Long id,
    Long userId,
    String userNickname,
    String userProfileImage,
    boolean isWriter,
    String location,
    String level,
    String content,
    Visibility visibility,
    LocalDate activityAt,
    int likeCount,
    int replyCount,
    boolean isLiked,
    LocalDateTime createdAt,

    List<FileResponse> files
) {

}
