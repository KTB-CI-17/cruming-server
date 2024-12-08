package com.ci.Cruming.timeline.service;

import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.location.repository.LocationRepository;
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
import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.follow.service.FollowService;
import com.ci.Cruming.timeline.validator.TimelineValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    @Autowired
    private FollowService followService;

    @Transactional
    public TimelineResponse createTimeline(User user, TimelineRequest request) {
        timelineValidator.validateTimelineRequest(request);
        
        Location location = locationRepository.findById(request.getLocationId())
            .orElseThrow(() -> new CrumingException(ErrorCode.LOCATION_NOT_FOUND));

        Timeline timeline = timelineMapper.toEntity(request, user, location);
        timeline = timelineRepository.save(timeline);
        
        return convertToResponse(timeline, user);
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
        return TimelineListResponse.fromEntity(timeline, currentUser);
    }

    private TimelineResponse convertToResponse(Timeline timeline, User currentUser) {
        return TimelineResponse.fromEntity(timeline, currentUser);
    }

    public Page<TimelineReplyResponse> getTimelineReplies(User currentUser, Long timelineId, Pageable pageable) {
        Timeline timeline = timelineRepository.findByIdAndDeletedAtIsNull(timelineId)
            .orElseThrow(() -> new CrumingException(ErrorCode.TIMELINE_NOT_FOUND));
            
        return timelineReplyRepository
            .findByTimelineAndParentIsNullOrderByCreatedAtAsc(timeline, pageable)
            .map(reply -> TimelineReplyResponse.fromEntity(reply, currentUser));
    }

    @Transactional(readOnly = true)
    public Page<TimelineListResponse> getMonthlyTimelines(User user, int year, int month, Pageable pageable) {
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime startOfNextMonth = startOfMonth.plusMonths(1);
        
        return timelineRepository.findByUserAndCreatedAtBetweenOrderByCreatedAtDesc(
                user, startOfMonth, startOfNextMonth, pageable)
            .map(timeline -> convertToListResponse(timeline, user));
    }

    @Transactional(readOnly = true)
    public Page<TimelineListResponse> getFollowingTimelines(User user, Pageable pageable) {
        List<Long> followingIds = followService.getAllFollowingIds(user.getId());
        
        return timelineRepository.findByUserIdInOrderByCreatedAtDesc(followingIds, pageable)
            .map(timeline -> convertToListResponse(timeline, user));
    }
} 