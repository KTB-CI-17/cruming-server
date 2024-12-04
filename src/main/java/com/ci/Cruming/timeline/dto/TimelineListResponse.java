package com.ci.Cruming.timeline.dto;

import java.time.LocalDateTime;

import com.ci.Cruming.timeline.entity.Timeline;

import lombok.Builder;

@Builder
public record TimelineListResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        TimelineUserDTO user,
        int replyCount
) {
    public static TimelineListResponse fromEntity(Timeline timeline) {
        return TimelineListResponse.builder()
            .id(timeline.getId())
            .content(timeline.getContent())
            .createdAt(timeline.getCreatedAt())
            .user(TimelineUserDTO.builder()
                .id(timeline.getUser().getId())
                .nickname(timeline.getUser().getNickname())
                .profileImageUrl(null)
                .build())
            .replyCount(timeline.getReplyCount())
            .build();
    }
}
