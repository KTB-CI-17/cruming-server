package com.ci.Cruming.timeline.controller;

import java.net.URI;
import java.time.LocalDate;

import com.ci.Cruming.timeline.dto.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.ci.Cruming.timeline.service.TimelineService;
import com.ci.Cruming.user.entity.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/timelines")
@RequiredArgsConstructor
public class TimelineController {
    private final TimelineService timelineService;
    
    @PostMapping
    public ResponseEntity<Void> createTimeline(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TimelineRequest request) {
        TimelineResponse response = timelineService.createTimeline(user, request);
        return ResponseEntity.created(URI.create("/api/v1/timelines/" + response.id())).build();
    }
    
    @DeleteMapping("/{timelineId}")
    public ResponseEntity<Void> deleteTimeline(
            @AuthenticationPrincipal User user,
            @PathVariable Long timelineId) {
        timelineService.deleteTimeline(user, timelineId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{timelineId}/likes/toggle")
    public ResponseEntity<Boolean> toggleTimelineLike(
            @AuthenticationPrincipal User user,
            @PathVariable Long timelineId) {
        boolean isLiked = timelineService.toggleTimelineLike(user, timelineId);
        return ResponseEntity.ok(isLiked);
    }
    
    @PostMapping("/{timelineId}/replies")
    public ResponseEntity<TimelineReplyResponse> createReply(
            @AuthenticationPrincipal User user,
            @PathVariable Long timelineId,
            @Valid @RequestBody TimelineReplyRequest request) {
        return ResponseEntity.ok(timelineService.createReply(user, timelineId, request));
    }
    
    @GetMapping("/users/{userId}")
    public ResponseEntity<Page<TimelineListResponse>> getUserTimelines(
            @AuthenticationPrincipal User user,
            @PathVariable Long userId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(timelineService.getUserTimelines(user, userId, pageable));
    }
    
    @GetMapping("/users/{userId}/date/{date}")
    public ResponseEntity<Page<TimelineListResponse>> getUserTimelinesByDate(
            @AuthenticationPrincipal User user,
            @PathVariable Long userId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(timelineService.getUserTimelinesByDate(user, userId, date, pageable));
    }
    
    @GetMapping("/{timelineId}/detail")
    public ResponseEntity<TimelineResponse> getTimelineDetail(
            @AuthenticationPrincipal User user,
            @PathVariable Long timelineId) {
        return ResponseEntity.ok(timelineService.getTimelineDetail(user, timelineId));
    }
    
    @GetMapping("/{timelineId}/replies")
    public ResponseEntity<Page<TimelineReplyResponse>> getTimelineReplies(
            @AuthenticationPrincipal User user,
            @PathVariable Long timelineId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(timelineService.getTimelineReplies(user, timelineId, pageable));
    }
}
