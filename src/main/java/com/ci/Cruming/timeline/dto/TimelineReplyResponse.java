package com.ci.Cruming.timeline.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record TimelineReplyResponse(
    Long id,
    Long userId,
    String content,
    LocalDateTime createdAt,
    String userProfile,
    String userNickname,
    boolean isWriter,
    Long childCount
) {
}