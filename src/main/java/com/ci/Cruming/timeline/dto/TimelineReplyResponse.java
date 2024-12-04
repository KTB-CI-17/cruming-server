package com.ci.Cruming.timeline.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.ci.Cruming.timeline.entity.TimelineReply;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record TimelineReplyResponse(
    @NotNull
    Long id,
    
    @NotNull
    TimelineUserDTO user,
    
    @NotBlank
    @Size(max = 1500)
    String content,
    
    List<TimelineReplyResponse> children,
    
    @NotNull
    LocalDateTime createdAt,
    
    LocalDateTime updatedAt
) {
    public static TimelineReplyResponse fromEntity(TimelineReply reply) {
        List<TimelineReplyResponse> childrenResponses = reply.getChildren().stream()
            .filter(child -> !child.isDeleted())
            .map(TimelineReplyResponse::fromEntity)
            .collect(Collectors.toList());

        return TimelineReplyResponse.builder()
            .id(reply.getId())
            .user(TimelineUserDTO.builder()
                .id(reply.getUser().getId())
                .nickname(reply.getUser().getNickname())
                .profileImageUrl(null)
                .build())
            .content(reply.getContent())
            .children(childrenResponses)
            .createdAt(reply.getCreatedAt())
            .updatedAt(reply.getUpdatedAt())
            .build();
    }
}