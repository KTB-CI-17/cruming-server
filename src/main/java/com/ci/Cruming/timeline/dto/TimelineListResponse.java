package com.ci.Cruming.timeline.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TimelineListResponse(
    Long id,
    String content,
    String level,
    String location,
    LocalDate activityAt,
    Long userId,
    String userNickname,
    boolean isWriter,
    String file
) {
}
