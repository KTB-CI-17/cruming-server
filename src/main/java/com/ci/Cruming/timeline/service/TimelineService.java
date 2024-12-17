package com.ci.Cruming.timeline.service;

import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.location.repository.LocationRepository;
import com.ci.Cruming.location.service.LocationService;
import com.ci.Cruming.timeline.dto.*;
import com.ci.Cruming.timeline.entity.Timeline;
import com.ci.Cruming.timeline.entity.TimelineLike;
import com.ci.Cruming.timeline.entity.TimelineReply;
import com.ci.Cruming.timeline.mapper.TimelineMapper;
import com.ci.Cruming.timeline.repository.TimelineLikeRepository;
import com.ci.Cruming.timeline.repository.TimelineReplyRepository;
import com.ci.Cruming.timeline.repository.TimelineRepository;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.user.repository.UserRepository;
import com.ci.Cruming.common.constants.FileTargetType;
import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.follow.service.FollowService;
import com.ci.Cruming.timeline.validator.TimelineValidator;
import com.ci.Cruming.file.service.FileService;
import com.ci.Cruming.file.dto.FileResponse;
import com.ci.Cruming.file.entity.FileMapping;
import com.ci.Cruming.file.dto.mapper.FileMapper;
import com.ci.Cruming.file.dto.FileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimelineService {
    private final TimelineRepository timelineRepository;
    private final TimelineLikeRepository timelineLikeRepository;
    private final TimelineReplyRepository timelineReplyRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final TimelineMapper timelineMapper;
    private final TimelineValidator timelineValidator;
    private final FileService fileService;
    private final FileMapper fileMapper;
    @Autowired
    private FollowService followService;
    private final LocationService locationService;

    @Transactional
    public TimelineResponse createTimeline(User user, TimelineRequest request, List<MultipartFile> files) {
        timelineValidator.validateTimelineRequest(request);

        Location location = locationService.getOrCreateLocation(request.getLocation());
        Timeline timeline = timelineMapper.toEntity(request, user, location);
        timeline = timelineRepository.save(timeline);
        
        // 파일 매핑 생성 및 설정
        if (files != null && !files.isEmpty()) {
            FileMapping fileMapping = createFileMapping(timeline.getId());
            timeline.setFileMapping(fileMapping);
            fileService.createFiles(user, fileMapping, files, 
                files.stream()
                    .map(file -> new FileRequest(file.getOriginalFilename(), 0))
                    .collect(Collectors.toList())
            );
        }

        List<FileResponse> fileResponses = fileService.getFilesByTimeline(timeline)
            .stream()
            .map(fileMapper::toFileResponse)
            .collect(Collectors.toList());

        return TimelineResponse.fromEntity(timeline, user, fileResponses);
    }

    @Transactional
    public void deleteTimeline(User user, Long timelineId) {
        Timeline timeline = timelineRepository.findByIdAndDeletedAtIsNull(timelineId)
            .orElseThrow(() -> new CrumingException(ErrorCode.TIMELINE_NOT_FOUND));

        timelineValidator.validateTimelineAuthor(timeline, user);

        List<TimelineReply> replies = timelineReplyRepository.findAllByTimelineAndDeletedAtIsNull(timeline);
        replies.forEach(reply -> timelineReplyRepository.delete(reply));
        
        timelineRepository.delete(timeline);
    }

    @Transactional
    public boolean toggleTimelineLike(User user, Long timelineId) {
        Timeline timeline = timelineRepository.findByIdAndDeletedAtIsNull(timelineId)
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

    @Transactional
    public TimelineReplyResponse createReply(User user, Long timelineId, TimelineReplyRequest request) {
        timelineValidator.validateReplyRequest(request);
        
        Timeline timeline = timelineRepository.findByIdAndDeletedAtIsNull(timelineId)
            .orElseThrow(() -> new CrumingException(ErrorCode.TIMELINE_NOT_FOUND));

        TimelineReply parent = request.getParentId() != null 
            ? timelineReplyRepository.findByIdAndDeletedAtIsNull(request.getParentId())
                .orElseThrow(() -> new CrumingException(ErrorCode.REPLY_NOT_FOUND))
            : null;

        TimelineReply reply = timelineMapper.toEntity(request, timeline, user, parent);
        reply = timelineReplyRepository.save(reply);
        
        return TimelineReplyResponse.fromEntity(reply, user);
    }

    public Page<TimelineListResponse> getUserTimelines(User currentUser, Long userId, Pageable pageable) {
        User targetUser = userRepository.findById(userId)
            .orElseThrow(() -> new CrumingException(ErrorCode.USER_NOT_FOUND));
            
        return timelineRepository.findByUserOrderByCreatedAtDesc(targetUser, pageable)
            .map(timeline -> convertToListResponse(timeline, currentUser));
    }
    
    public Page<TimelineListResponse> getUserTimelinesByDate(User currentUser, Long userId, LocalDate date, Pageable pageable) {
        User targetUser = userRepository.findById(userId)
            .orElseThrow(() -> new CrumingException(ErrorCode.USER_NOT_FOUND));
            
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        
        return timelineRepository.findByUserAndActivityAtBetweenOrderByActivityAtDesc(
                targetUser, startOfDay, endOfDay, pageable)
            .map(timeline -> convertToListResponse(timeline, currentUser));
    }
    
    public TimelineResponse getTimelineDetail(User currentUser, Long timelineId) {
        Timeline timeline = timelineRepository.findByIdAndDeletedAtIsNull(timelineId)
            .orElseThrow(() -> new CrumingException(ErrorCode.TIMELINE_NOT_FOUND));
            
        return convertToResponse(timeline, currentUser);
    }

    private TimelineListResponse convertToListResponse(Timeline timeline, User currentUser) {
        List<FileResponse> files = fileService.getFilesByTimeline(timeline)
            .stream()
            .map(fileMapper::toFileResponse)
            .collect(Collectors.toList());
        return TimelineListResponse.fromEntity(timeline, currentUser, files);
    }

    private TimelineResponse convertToResponse(Timeline timeline, User currentUser) {
        List<FileResponse> files = fileService.getFilesByTimeline(timeline)
            .stream()
            .map(fileMapper::toFileResponse)
            .collect(Collectors.toList());
        return TimelineResponse.fromEntity(timeline, currentUser, files);
    }

    public Page<TimelineReplyResponse> getTimelineReplies(User currentUser, Long timelineId, Pageable pageable) {
        Timeline timeline = timelineRepository.findByIdAndDeletedAtIsNull(timelineId)
            .orElseThrow(() -> new CrumingException(ErrorCode.TIMELINE_NOT_FOUND));
            
        return timelineReplyRepository
            .findByTimelineAndParentIsNullOrderByCreatedAtAsc(timeline, pageable)
            .map(reply -> TimelineReplyResponse.fromEntity(reply, currentUser));
    }

    @Transactional(readOnly = true)
    public List<TimelineListResponse> getMonthlyTimelines(User user, int year, int month) {
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime startOfNextMonth = startOfMonth.plusMonths(1);
        
        return timelineRepository.findByUserAndCreatedAtBetweenOrderByCreatedAtDesc(
                user, startOfMonth, startOfNextMonth)
            .stream()
            .map(timeline -> convertToListResponse(timeline, user))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TimelineListResponse> getFollowingTimelines(User user, Pageable pageable) {
        List<Long> followingIds = followService.getAllFollowingIds(user.getId());
        
        return timelineRepository.findByUserIdInOrderByCreatedAtDesc(followingIds, pageable)
            .map(timeline -> convertToListResponse(timeline, user));
    }

    private FileMapping createFileMapping(Long timelineId) {
        return FileMapping.builder()
                .targetType(FileTargetType.TIMELINE)
                .targetId(timelineId)
                .build();
    }
} 