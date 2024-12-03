package com.ci.Cruming.timeline.service;

import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.location.repository.LocationRepository;
import com.ci.Cruming.timeline.dto.*;
import com.ci.Cruming.timeline.entity.Timeline;
import com.ci.Cruming.timeline.entity.TimelineLike;
import com.ci.Cruming.timeline.entity.TimelineReply;
import com.ci.Cruming.timeline.repository.TimelineLikeRepository;
import com.ci.Cruming.timeline.repository.TimelineReplyRepository;
import com.ci.Cruming.timeline.repository.TimelineRepository;
import com.ci.Cruming.user.dto.UserDTO;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public TimelineResponse createTimeline(User user, TimelineRequest request) {
        Location location = locationRepository.findById(request.getLocationId())
            .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        Timeline timeline = Timeline.builder()
            .user(user)
            .location(location)
            .level(request.getLevel())
            .content(request.getContent())
            .visibility(request.getVisibility())
            .activityAt(request.getActivityAt())
            .build();

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

        timeline.delete();
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

        TimelineReply parent = null;
        if (request.getParentId() != null) {
            parent = timelineReplyRepository.findByIdAndDeletedAtIsNull(request.getParentId())
                .orElseThrow(() -> new EntityNotFoundException("Parent reply not found"));
        }

        TimelineReply reply = TimelineReply.builder()
            .timeline(timeline)
            .parent(parent)
            .user(user)
            .content(request.getContent())
            .build();

        reply = timelineReplyRepository.save(reply);
        return convertToReplyResponse(reply);
    }

    public List<TimelineResponse> getUserTimelines(User currentUser, Long userId, int page, int limit) {
        User targetUser = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
            
        Pageable pageable = PageRequest.of(page, limit);
        Page<Timeline> timelinePage = timelineRepository.findByUserOrderByCreatedAtDesc(targetUser, pageable);
        
        return timelinePage.stream()
            .filter(timeline -> !timeline.isDeleted())
            .map(timeline -> convertToResponse(timeline, currentUser))
            .collect(Collectors.toList());
    }
    
    public List<TimelineResponse> getUserTimelinesByDate(User currentUser, Long userId, LocalDate date, int page, int limit) {
        User targetUser = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
            
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        
        Pageable pageable = PageRequest.of(page, limit);
        Page<Timeline> timelinePage = timelineRepository.findByUserAndActivityAtBetweenOrderByActivityAtDesc(
                targetUser, startOfDay, endOfDay, pageable);
        
        return timelinePage.stream()
            .filter(timeline -> !timeline.isDeleted())
            .map(timeline -> convertToResponse(timeline, currentUser))
            .collect(Collectors.toList());
    }
    
    public TimelineResponse getTimelineDetail(User currentUser, Long timelineId) {
        Timeline timeline = timelineRepository.findByIdAndDeletedAtIsNull(timelineId)
            .orElseThrow(() -> new EntityNotFoundException("Timeline not found"));
            
        return convertToResponse(timeline, currentUser);
    }

    private TimelineResponse convertToResponse(Timeline timeline, User currentUser) {
        boolean isLiked = timelineLikeRepository.existsByTimelineAndUser(timeline, currentUser);
        List<TimelineReplyResponse> replies = timelineReplyRepository
            .findByTimelineAndParentIsNullOrderByCreatedAtAsc(timeline)
            .stream()
            .map(this::convertToReplyResponse)
            .collect(Collectors.toList());

        return new TimelineResponse(
            timeline.getId(),
            UserDTO.fromEntity(timeline.getUser()),
            timeline.getLocation(),
            timeline.getLevel(),
            timeline.getContent(),
            timeline.getVisibility(),
            timeline.getActivityAt(),
            timeline.getLikeCount(),
            timeline.getReplyCount(),
            isLiked,
            replies,
            timeline.getCreatedAt()
        );
    }

    private TimelineReplyResponse convertToReplyResponse(TimelineReply reply) {
        List<TimelineReplyResponse> children = reply.getChildren().stream()
            .filter(child -> !child.isDeleted())
            .map(this::convertToReplyResponse)
            .collect(Collectors.toList());

        return new TimelineReplyResponse(
            reply.getId(),
            UserDTO.fromEntity(reply.getUser()),
            reply.getContent(),
            children,
            reply.getCreatedAt(),
            reply.getUpdatedAt()
        );
    }
} 