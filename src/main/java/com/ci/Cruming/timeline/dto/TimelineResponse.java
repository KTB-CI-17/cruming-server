package com.ci.Cruming.timeline.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ci.Cruming.common.constants.Visibility;
import com.ci.Cruming.file.dto.FileResponse;
import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.timeline.entity.Timeline;
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
    TimelineUserDTO user,
    
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
    
    @NotNull
    LocalDateTime createdAt,

    List<FileResponse> files
) {
    public static TimelineResponse fromEntity(Timeline timeline, User currentUser, List<FileResponse> files) {
        boolean isLiked = timeline.getLikes().stream()
            .anyMatch(like -> like.getUser().getId().equals(currentUser.getId()));

        return TimelineResponse.builder()
            .id(timeline.getId())
            .user(TimelineUserDTO.builder()
                .id(timeline.getUser().getId())
                .nickname(timeline.getUser().getNickname())
                .profileImageUrl(null)
                .build())
            .location(timeline.getLocation())
            .level(timeline.getLevel())
            .content(timeline.getContent())
            .visibility(timeline.getVisibility())
            .activityAt(timeline.getActivityAt())
            .likeCount(timeline.getLikeCount())
            .replyCount(timeline.getReplyCount())
            .isLiked(isLiked)
            .createdAt(timeline.getCreatedAt())
            .files(files)
            .build();
    }
}
