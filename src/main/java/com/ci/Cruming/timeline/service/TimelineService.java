package com.ci.Cruming.timeline.service;

import com.ci.Cruming.common.constants.FileTargetType;
import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.file.dto.FileResponse;
import com.ci.Cruming.file.entity.FileMapping;
import com.ci.Cruming.file.service.FileService;
import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.location.service.LocationService;
import com.ci.Cruming.timeline.dto.TimelineListResponse;
import com.ci.Cruming.timeline.dto.TimelineRequest;
import com.ci.Cruming.timeline.dto.TimelineResponse;
import com.ci.Cruming.timeline.entity.Timeline;
import com.ci.Cruming.timeline.entity.TimelineLike;
import com.ci.Cruming.timeline.mapper.TimelineMapper;
import com.ci.Cruming.timeline.repository.TimelineLikeRepository;
import com.ci.Cruming.timeline.repository.TimelineRepository;
import com.ci.Cruming.timeline.validator.TimelineValidator;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimelineService {

    private final LocationService locationService;
    private final FileService fileService;
    private final TimelineValidator timelineValidator;
    private final TimelineMapper timelineMapper;
    private final TimelineRepository timelineRepository;
    private final UserRepository userRepository;
    private final TimelineLikeRepository timelineLikeRepository;

    @Transactional
    public void createTimeline(User user, TimelineRequest request, List<MultipartFile> files) {
        timelineValidator.validateTimelineRequest(request);

        Location location = locationService.getOrCreateLocation(request.getLocation());
        Timeline timeline = timelineRepository.save(timelineMapper.toEntity(request, user, location));
        FileMapping fileMapping = createFileMapping(timeline.getId());

        fileMapping = fileService.createFiles(user, fileMapping, files, request.getFileRequests());
        timeline.setFileMapping(fileMapping);
    }

    private FileMapping createFileMapping(Long timelineId) {
        return FileMapping.builder()
                .targetType(FileTargetType.TIMELINE)
                .targetId(timelineId)
                .build();
    }

    public Page<TimelineListResponse> getMonthlyTimelines(User user, int year, int month, Pageable pageable) {
        LocalDate startDate = getStartDate(year, month);
        LocalDate endDate = getEndDate(year, month);

        return timelineRepository.findByActivityAtBetweenOrderByActivityAtDesc(startDate, endDate, pageable)
                .map(timeline -> {
                    FileResponse fileResponse = fileService.getFirstFileByMappingId(timeline.getFileMapping());
                    return timelineMapper.toTimelineListResponse(timeline, user, fileResponse);
                });
    }

    public List<LocalDate> getActivityDate(User user, int year, int month) {
        LocalDate startDate = getStartDate(year, month);
        LocalDate endDate = getEndDate(year, month);

        return timelineRepository.findDistinctActivityAtByUserBetween(user, startDate, endDate);
    }

    private LocalDate getStartDate(int year, int month) {
        return LocalDate.of(year, month, 1);
    }

    private LocalDate getEndDate(int year, int month) {
        return YearMonth.of(year, month).atEndOfMonth();
    }

    public Page<TimelineListResponse> getUserTimelines(User user, Long userId, Pageable pageable) {
        return timelineRepository.findByUserOrderByActivityAtDesc(getUser(userId), pageable)
                .map(timeline ->
                        timelineMapper.toTimelineListResponse(
                                timeline,
                                user,
                                fileService.getFirstFileByMappingId(timeline.getFileMapping())
                        )
                );
    }

    public Page<TimelineListResponse> getFollowingTimelines(User user, Pageable pageable) {
        return timelineRepository.findTimelinesByFollowerId(user, pageable)
                .map(timeline ->
                        timelineMapper.toTimelineListResponse(
                                timeline,
                                user,
                                fileService.getFirstFileByMappingId(timeline.getFileMapping())
                        )
                );
    }

    public TimelineResponse getTimelineDetail(User currentUser, Long timelineId) {
        Timeline timeline = timelineRepository.findById(timelineId)
            .orElseThrow(() -> new CrumingException(ErrorCode.TIMELINE_NOT_FOUND));

        return timelineMapper.toTimelineResponse(timeline, currentUser);
    }

    @Transactional
    public void deleteTimeline(User user, Long timelineId) {
        Timeline timeline = timelineRepository.findById(timelineId)
                .orElseThrow(() -> new CrumingException(ErrorCode.TIMELINE_NOT_FOUND));

        timelineValidator.validateTimelineAuthor(timeline, user);
        timelineRepository.delete(timeline);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CrumingException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public boolean toggleTimelineLike(User user, Long timelineId) {
        Timeline timeline = timelineRepository.findById(timelineId)
            .orElseThrow(() -> new CrumingException(ErrorCode.TIMELINE_NOT_FOUND));

        return timelineLikeRepository.findByTimelineAndUser(timeline, user)
            .map(like -> {
                timelineLikeRepository.delete(like);
                return false;
            })
            .orElseGet(() -> {
                TimelineLike like = TimelineLike.builder()
                    .timeline(timeline)
                    .user(user)
                    .build();
                timelineLikeRepository.save(like);
                return true;
            });
    }

}
