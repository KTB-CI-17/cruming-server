package com.ci.Cruming.timeline.dto;

import java.time.LocalDateTime;
import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.timeline.entity.Timeline;
import com.ci.Cruming.user.entity.User;

import lombok.Builder;

@Builder
public record TimelineListResponse(
    Long id,
    String content,
    String level,
    Location location,
    LocalDateTime createdAt,
    Long userId,
    String userNickname,
    boolean isWriter
) {
    public static TimelineListResponse fromEntity(Timeline timeline, User currentUser) {
        return TimelineListResponse.builder()
            .id(timeline.getId())
            .content(timeline.getContent())
            .level(timeline.getLevel())
            .location(timeline.getLocation())
            .createdAt(timeline.getCreatedAt())
            .userId(timeline.getUser().getId())
            .userNickname(timeline.getUser().getNickname())
            .isWriter(timeline.getUser().getId().equals(currentUser.getId()))
            .build();
    }
}
