package com.ci.Cruming.timeline.controller;

import com.ci.Cruming.timeline.dto.*;

import com.ci.Cruming.timeline.service.TimelineReplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.ci.Cruming.user.entity.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("/api/v1/timelines")
@RequiredArgsConstructor
@Tag(name = "Timeline Reply", description = "타임라인 댓글 API")
public class TimelineReplyController {

    private final TimelineReplyService timelineReplyService;

    @Operation(summary = "타임라인 댓글 작성", description = "타임라인에 새로운 댓글을 작성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "댓글 작성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "타임라인을 찾을 수 없음")
    })
    @PostMapping("/{timelineId}/replies")
    public ResponseEntity<Void> createReply(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "타임라인 ID") @PathVariable Long timelineId,
            @Parameter(description = "댓글 작성 정보") @Valid @RequestBody TimelineReplyRequest request) {
        timelineReplyService.createReply(user, timelineId, request);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{timelineId}/replies/{parentId}")
    @Operation(summary = "대댓글 작성", description = "게시글에 대댓글을 작성합니다.")
    public ResponseEntity<Void> createReply(
            @AuthenticationPrincipal User user,
            @PathVariable Long timelineId,
            @PathVariable Long parentId,
            @RequestBody TimelineReplyRequest request
    ) {
        timelineReplyService.createTimelineReply(user, request, timelineId, parentId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/replies/{replyId}")
    @Operation(summary = "댓글 수정", description = "게시글에 댓글을 수정합니다.")
    public ResponseEntity<Void> updateReply(
            @AuthenticationPrincipal User user,
            @PathVariable Long replyId,
            @RequestBody TimelineReplyRequest request
    ) {
        timelineReplyService.updateTimelineReply(user, request, replyId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/replies/{replyId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    public ResponseEntity<Void> deleteReply(
            @AuthenticationPrincipal User user,
            @PathVariable Long replyId
    ) {
        timelineReplyService.deleteTimelineReply(user, replyId);
        return ResponseEntity.ok().build();
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
            @Parameter(description = "페이지네이션 정보") Pageable pageable) {
        return ResponseEntity.ok(timelineReplyService.findTimelineReplyList(user, pageable, timelineId));
    }

    @GetMapping("/replies/{parentId}/children")
    public ResponseEntity<Page<TimelineReplyResponse>> getChildReplies(
            @AuthenticationPrincipal User user,
            @PathVariable Long parentId,
            Pageable pageable) {
        return ResponseEntity.ok(timelineReplyService.findTimelineChildReplyList(user, parentId, pageable));
    }
}
