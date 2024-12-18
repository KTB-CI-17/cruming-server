package com.ci.Cruming.timeline.dto;

import java.time.LocalDateTime;

import com.ci.Cruming.location.dto.LocationDTO;
import com.ci.Cruming.timeline.entity.Timeline;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.file.dto.FileResponse;
import lombok.Builder;
import java.util.List;

@Builder
public record TimelineListResponse(
    Long id,
    String content,
    String level,
    LocationDTO location,
    LocalDateTime activityAt,
    Long userId,
    String userNickname,
    boolean isWriter,
    List<FileResponse> files
) {
    public static TimelineListResponse fromEntity(Timeline timeline, User currentUser, List<FileResponse> files) {
        return TimelineListResponse.builder()
            .id(timeline.getId())
            .content(timeline.getContent())
            .level(timeline.getLevel())
            .location(timeline.getLocation() != null ? 
                     LocationDTO.fromEntity(timeline.getLocation()) : null)
            .activityAt(timeline.getActivityAt())
            .userId(timeline.getUser().getId())
            .userNickname(timeline.getUser().getNickname())
            .isWriter(timeline.getUser().getId().equals(currentUser.getId()))
            .files(files)
            .build();
    }
}
