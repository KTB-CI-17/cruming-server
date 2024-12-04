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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public TimelineResponse createTimeline(User user, TimelineRequest request) {
        Location location = locationRepository.findById(request.getLocationId())
            .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        Timeline timeline = timelineMapper.toEntity(request, user, location);
        timeline = timelineRepository.save(timeline);
        
        return convertToResponse(timeline, user);
    }

    @Transactional
    public void deleteTimeline(User user, Long timelineId) {
        Timeline timeline = timelineRepository.findByIdAndDeletedAtIsNull(timelineId)
            .orElseThrow(() -> new EntityNotFoundException("Timeline not found"));

        if (!timeline.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Not authorized to delete this timeline");
        }

        List<TimelineReply> replies = timelineReplyRepository.findAllByTimelineAndDeletedAtIsNull(timeline);
        replies.forEach(reply -> timelineReplyRepository.delete(reply));
        
        timelineRepository.delete(timeline);
    }

    @Transactional
    public void likeTimeline(User user, Long timelineId) {
        Timeline timeline = timelineRepository.findByIdAndDeletedAtIsNull(timelineId)
            .orElseThrow(() -> new EntityNotFoundException("Timeline not found"));

        if (!timelineLikeRepository.existsByTimelineAndUser(timeline, user)) {
            TimelineLike like = TimelineLike.builder()
                .timeline(timeline)
                .user(user)
                .build();
            timelineLikeRepository.save(like);
        }
    }

    @Transactional
    public void unlikeTimeline(User user, Long timelineId) {
        Timeline timeline = timelineRepository.findByIdAndDeletedAtIsNull(timelineId)
            .orElseThrow(() -> new EntityNotFoundException("Timeline not found"));

        timelineLikeRepository.findByTimelineAndUser(timeline, user)
            .ifPresent(timelineLikeRepository::delete);
    }

    @Transactional
    public TimelineReplyResponse createReply(User user, Long timelineId, TimelineReplyRequest request) {
        Timeline timeline = timelineRepository.findByIdAndDeletedAtIsNull(timelineId)
            .orElseThrow(() -> new EntityNotFoundException("Timeline not found"));

        TimelineReply parent = request.getParentId() != null 
            ? timelineReplyRepository.findByIdAndDeletedAtIsNull(request.getParentId())
                .orElseThrow(() -> new EntityNotFoundException("Parent reply not found"))
            : null;

        TimelineReply reply = timelineMapper.toEntity(request, timeline, user, parent);
        reply = timelineReplyRepository.save(reply);
        
        return TimelineReplyResponse.fromEntity(reply, user);
    }

    public Page<TimelineListResponse> getUserTimelines(User currentUser, Long userId, Pageable pageable) {
        User targetUser = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
            
        return timelineRepository.findByUserOrderByCreatedAtDesc(targetUser, pageable)
            .map(timeline -> convertToListResponse(timeline, currentUser));
    }
    
    public Page<TimelineListResponse> getUserTimelinesByDate(User currentUser, Long userId, LocalDate date, Pageable pageable) {
        User targetUser = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
            
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        
        return timelineRepository.findByUserAndActivityAtBetweenOrderByActivityAtDesc(
                targetUser, startOfDay, endOfDay, pageable)
            .map(timeline -> convertToListResponse(timeline, currentUser));
    }
    
    public TimelineResponse getTimelineDetail(User currentUser, Long timelineId) {
        Timeline timeline = timelineRepository.findByIdAndDeletedAtIsNull(timelineId)
            .orElseThrow(() -> new EntityNotFoundException("Timeline not found"));
            
        return convertToResponse(timeline, currentUser);
    }

    private TimelineListResponse convertToListResponse(Timeline timeline, User currentUser) {
        return TimelineListResponse.fromEntity(timeline, currentUser);
    }

    private TimelineResponse convertToResponse(Timeline timeline, User currentUser) {
        return TimelineResponse.fromEntity(timeline, currentUser);
    }


    @Transactional
    public boolean toggleTimelineLike(User user, Long timelineId) {
        Timeline timeline = timelineRepository.findByIdAndDeletedAtIsNull(timelineId)
            .orElseThrow(() -> new EntityNotFoundException("Timeline not found"));

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

    public Page<TimelineReplyResponse> getTimelineReplies(User currentUser, Long timelineId, Pageable pageable) {
        Timeline timeline = timelineRepository.findByIdAndDeletedAtIsNull(timelineId)
            .orElseThrow(() -> new EntityNotFoundException("Timeline not found"));
            
        return timelineReplyRepository
            .findByTimelineAndParentIsNullOrderByCreatedAtAsc(timeline, pageable)
            .map(reply -> TimelineReplyResponse.fromEntity(reply, currentUser));
    }
} 