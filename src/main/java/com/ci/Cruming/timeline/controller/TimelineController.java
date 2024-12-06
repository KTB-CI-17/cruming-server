package com.ci.Cruming.timeline.controller;

import java.net.URI;
import java.time.LocalDate;

import com.ci.Cruming.timeline.dto.*;

import org.hibernate.validator.constraints.Range;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/timelines")
@RequiredArgsConstructor
@Tag(name = "Timeline", description = "타임라인 API")
public class TimelineController {
    private final TimelineService timelineService;
    
    @Operation(summary = "타임라인 생성", description = "새로운 타임라인을 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "타임라인 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "위치 정보를 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<Void> createTimeline(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "타임라인 생성 정보") @Valid @RequestBody TimelineRequest request) {
        TimelineResponse response = timelineService.createTimeline(user, request);
        return ResponseEntity.created(URI.create("/api/v1/timelines/" + response.id())).build();
    }
    
    @Operation(summary = "타임라인 삭제", description = "특정 타임라인을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "타임라인 삭제 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "타임라인�� 찾을 수 없음")
    })
    @DeleteMapping("/{timelineId}")
    public ResponseEntity<Void> deleteTimeline(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "삭제할 타임라인 ID") @PathVariable Long timelineId) {
        timelineService.deleteTimeline(user, timelineId);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "타임라인 좋아요 토글", description = "타임라인 좋아요를 추가하거나 취소합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "좋아요 토글 성공"),
        @ApiResponse(responseCode = "404", description = "타임라인을 찾을 수 없음")
    })
    @PostMapping("/{timelineId}/likes/toggle")
    public ResponseEntity<Boolean> toggleTimelineLike(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "타임라인 ID") @PathVariable Long timelineId) {
        boolean isLiked = timelineService.toggleTimelineLike(user, timelineId);
        return ResponseEntity.ok(isLiked);
    }
    
    @Operation(summary = "타임라인 댓글 작성", description = "타임라인에 새로운 댓글을 작성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "댓글 작성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "타임라인을 찾을 수 없음")
    })
    @PostMapping("/{timelineId}/replies")
    public ResponseEntity<TimelineReplyResponse> createReply(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "타임라인 ID") @PathVariable Long timelineId,
            @Parameter(description = "댓글 작성 정보") @Valid @RequestBody TimelineReplyRequest request) {
        return ResponseEntity.ok(timelineService.createReply(user, timelineId, request));
    }
    
    @Operation(summary = "사용자 타임라인 조회", description = "특정 사용자의 모든 타임라인을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<Page<TimelineListResponse>> getUserTimelines(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "조회할 사용자 ID") @PathVariable Long userId,
            @Parameter(description = "페이지네이션 정보") @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(timelineService.getUserTimelines(user, userId, pageable));
    }
    
    @Operation(summary = "날짜별 타임라인 조회", description = "특정 사용자의 특정 날짜 타임라인을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/users/{userId}/date/{date}")
    public ResponseEntity<Page<TimelineListResponse>> getUserTimelinesByDate(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "조회할 사용자 ID") @PathVariable Long userId,
            @Parameter(description = "조회할 날짜 (yyyy-MM-dd)") 
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @Parameter(description = "페이지네이션 정보") @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(timelineService.getUserTimelinesByDate(user, userId, date, pageable));
    }
    
    @Operation(summary = "타임라인 상세 조회", description = "특정 타임라인의 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "타임라인을 찾을 수 없음")
    })
    @GetMapping("/{timelineId}/detail")
    public ResponseEntity<TimelineResponse> getTimelineDetail(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "조회할 타임라인 ID") @PathVariable Long timelineId) {
        return ResponseEntity.ok(timelineService.getTimelineDetail(user, timelineId));
    }
    
    @Operation(summary = "타임라인 댓글 조회", description = "특정 타임라인의 댓글 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "타임라인을 찾을 수 없음")
    })
    @GetMapping("/{timelineId}/replies")
    public ResponseEntity<Page<TimelineReplyResponse>> getTimelineReplies(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "타임라인 ID") @PathVariable Long timelineId,
            @Parameter(description = "페이지네이션 정보") @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(timelineService.getTimelineReplies(user, timelineId, pageable));
    }
    
    @Operation(summary = "팔로잉 타임라인 조회", description = "팔로우한 사용자들의 타임라인을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/following")
    public ResponseEntity<Page<TimelineListResponse>> getFollowingTimelines(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "페이지네이션 정보") @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(timelineService.getFollowingTimelines(user, pageable));
    }
    
    @Operation(summary = "월별 타임라인 조회", description = "특정 연월의 타임라인을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 월 입력")
    })
    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<Page<TimelineListResponse>> getMonthlyTimelines(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "조회할 연도") @PathVariable int year,
            @Parameter(description = "조회할 월 (1-12)") @PathVariable @Range(min = 1, max = 12) int month,
            @Parameter(description = "페이지네이션 정보") @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(timelineService.getMonthlyTimelines(user, year, month, pageable));
    }
}
