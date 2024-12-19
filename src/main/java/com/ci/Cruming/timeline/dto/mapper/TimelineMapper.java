package com.ci.Cruming.timeline.dto.mapper;

import com.ci.Cruming.common.utils.FileUtils;
import com.ci.Cruming.file.dto.FileResponse;
import com.ci.Cruming.file.dto.mapper.FileMapper;
import com.ci.Cruming.file.service.FileService;
import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.timeline.dto.TimelineEditInfo;
import com.ci.Cruming.timeline.dto.TimelineListResponse;
import com.ci.Cruming.timeline.dto.TimelineRequest;
import com.ci.Cruming.timeline.dto.TimelineResponse;
import com.ci.Cruming.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.ci.Cruming.timeline.entity.Timeline;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TimelineMapper {

    private final FileUtils fileUtils;
    private final FileMapper fileMapper;
    private final FileService fileService;

    public Timeline toEntity(TimelineRequest request, User user, Location location) {
        return Timeline.builder()
            .user(user)
            .location(location)
            .level(request.getLevel())
            .content(request.getContent())
            .visibility(request.getVisibility())
            .activityAt(request.getActivityAt())
            .build();
    }

    public TimelineListResponse toTimelineListResponse(Timeline timeline, User loginUser, FileResponse fileResponse) {
        String location = Optional.ofNullable(timeline.getLocation())
                .map(Location::getPlaceName)
                .orElse(null);
        boolean isWriter = timeline.getUser().getId().equals(loginUser.getId());
        String fileUrl = fileResponse == null ? null : fileResponse.url();

        return TimelineListResponse.builder()
                .id(timeline.getId())
                .content(timeline.getContent())
                .level(timeline.getLevel())
                .location(location)
                .userId(timeline.getUser().getId())
                .userNickname(timeline.getUser().getNickname())
                .isWriter(isWriter)
                .file(fileUrl)
                .activityAt(timeline.getActivityAt())
                .build();
    }

    public TimelineResponse toTimelineResponse(Timeline timeline, User user) {
        List<FileResponse> files = fileService.getFilesByTimeline(timeline)
                .stream()
                .map(fileMapper::toFileResponse)
                .collect(Collectors.toList());

            boolean isLiked = timeline.getLikes().stream()
                    .anyMatch(like -> like.getUser().getId().equals(user.getId()));

            String location = timeline.getLocation() == null ? null : timeline.getLocation().getPlaceName();

        return TimelineResponse.builder()
                .id(timeline.getId())
                .userId(timeline.getUser().getId())
                .userNickname(timeline.getUser().getNickname())
                .userProfileImage(fileUtils.generatePresignedUrl(timeline.getUser().getProfileImageUrl()))
                .location(location)
                .level(timeline.getLevel())
                .isWriter(user.getId().equals(timeline.getUser().getId()))
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

    public TimelineEditInfo toTimelineEditInfo(Timeline timeline, List<FileResponse> files) {
        return TimelineEditInfo.builder()
                .id(timeline.getId())
                .level(timeline.getLevel())
                .content(timeline.getContent())
                .visibility(timeline.getVisibility())
                .activityAt(timeline.getActivityAt())
                .location(Optional.ofNullable(timeline.getLocation())
                        .map(loc -> new TimelineEditInfo.Location(
                                loc.getPlaceName(),
                                loc.getAddress(),
                                loc.getLatitude(),
                                loc.getLongitude()
                        ))
                        .orElse(null))
                .files(files)
                .build();
    }
}
