package com.ci.Cruming.timeline.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.ci.Cruming.timeline.dto.TimelineReplyRequest;
import com.ci.Cruming.timeline.dto.TimelineReplyResponse;
import com.ci.Cruming.timeline.dto.TimelineRequest;
import com.ci.Cruming.timeline.dto.TimelineResponse;
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
    public ResponseEntity<TimelineResponse> createTimeline(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TimelineRequest request) {
        return ResponseEntity.ok(timelineService.createTimeline(user, request));
    }
    
    @DeleteMapping("/{timelineId}")
    public ResponseEntity<Void> deleteTimeline(
            @AuthenticationPrincipal User user,
            @PathVariable Long timelineId) {
        timelineService.deleteTimeline(user, timelineId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{timelineId}/likes")
    public ResponseEntity<Void> likeTimeline(
            @AuthenticationPrincipal User user,
            @PathVariable Long timelineId) {
        timelineService.likeTimeline(user, timelineId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{timelineId}/likes")
    public ResponseEntity<Void> unlikeTimeline(
            @AuthenticationPrincipal User user,
            @PathVariable Long timelineId) {
        timelineService.unlikeTimeline(user, timelineId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{timelineId}/replies")
    public ResponseEntity<TimelineReplyResponse> createReply(
            @AuthenticationPrincipal User user,
            @PathVariable Long timelineId,
            @Valid @RequestBody TimelineReplyRequest request) {
        return ResponseEntity.ok(timelineService.createReply(user, timelineId, request));
    }
}
