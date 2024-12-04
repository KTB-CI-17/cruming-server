package com.ci.Cruming.timeline.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.ci.Cruming.common.constants.Visibility;
import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.timeline.entity.Timeline;
import com.ci.Cruming.timeline.entity.TimelineReply;
import com.ci.Cruming.user.dto.UserDTO;
import com.ci.Cruming.user.entity.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;


@Builder
public record TimelineResponse(
    @NotNull
    Long id,
    
    @NotNull
    UserDTO userDTO,
    
    @NotNull
    Location location,
    
    @NotBlank
    @Size(max = 20)
    String level,
    
    @NotBlank
    @Size(max = 3000)
    String content,
    
    @NotNull
    Visibility visibility,
    
    @NotNull
    LocalDateTime activityAt,
    
    @PositiveOrZero
    int likeCount,
    
    @PositiveOrZero
    int replyCount,
    
    boolean isLiked,
    
    List<TimelineReplyResponse> replies,
    
    @NotNull
    LocalDateTime createdAt
) {
    public static TimelineResponse fromEntity(Timeline timeline, User currentUser, List<TimelineReply> replies) {
        boolean isLiked = timeline.getLikes().stream()
            .anyMatch(like -> like.getUser().getId().equals(currentUser.getId()));

        List<TimelineReplyResponse> replyResponses = replies.stream()
            .map(TimelineReplyResponse::fromEntity)
            .collect(Collectors.toList());

        return TimelineResponse.builder()
            .id(timeline.getId())
            .userDTO(UserDTO.fromEntity(timeline.getUser()))
            .location(timeline.getLocation())
            .level(timeline.getLevel())
            .content(timeline.getContent())
            .visibility(timeline.getVisibility())
            .activityAt(timeline.getActivityAt())
            .likeCount(timeline.getLikeCount())
            .replyCount(timeline.getReplyCount())
            .isLiked(isLiked)
            .replies(replyResponses)
            .createdAt(timeline.getCreatedAt())
            .build();
    }
}
