package com.ci.Cruming.timeline.controller;

import com.ci.Cruming.timeline.dto.*;
import com.ci.Cruming.timeline.service.TimelineService;
import com.ci.Cruming.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/timelines")
@RequiredArgsConstructor
@Tag(name = "Timeline", description = "타임라인 API")
@Slf4j
public class TimelineController {

    private final TimelineService timelineService;

    @Operation(summary = "타임라인 생성", description = "새로운 타임라인을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "타임라인 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "위치 정보를 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<TimelineResponse> createTimeline(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "타임라인 생성 정보") @Valid @RequestPart TimelineRequest request,
            @Parameter(description = "첨부 파일 목록") @RequestPart(required = false) List<MultipartFile> files) {
        timelineService.createTimeline(user, request, files);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/edit/{timelineId}")
    @Operation(
            summary = "타임라인 수정을 위한 데이터 조회",
            description = "타임라인 수정에 필요한 기존 타임라인 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "타임라인을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "타임라인에 필수 데이터가 보함되지 않음")
    })
    public ResponseEntity<TimelineEditInfo> editTimeline(
            @Parameter(description = "수정할 타임라인 ID") @PathVariable Long timelineId) {
        TimelineEditInfo timelineEditInfo = timelineService.findTimelineEditInfo(timelineId);
        return ResponseEntity.ok(timelineEditInfo);
    }

    @PatchMapping("/{timelineId}")
    @Operation(
            summary = "타임라인 수정",
            description = "기존 타임라인을 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "타임라인을 찾을 수 없음")
    })
    public ResponseEntity<Void> updateTimeline(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "수정할 타임라인 ID") @PathVariable Long timelineId,
            @Parameter(description = "수정할 타임라인 정보") @RequestPart(value = "request") TimelineEditRequest request,
            @Parameter(description = "수정할 첨부 파일 목록") @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        log.info("request={}", request);
        timelineService.updateTimeline(user, timelineId, request, files);
        return ResponseEntity.noContent().build();
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
            @Parameter(description = "페이징 정보") Pageable pageable) {
        return ResponseEntity.ok(timelineService.getMonthlyTimelines(user, year, month, pageable));
    }

    @Operation(summary = "월별 활동일자 리스트 조회", description = "월별로 활동일자를 중복없이 리스트로 조회합니다.")
    @GetMapping("/activity/{year}/{month}")
    public ResponseEntity<List<LocalDate>> getActivityDate(
            @AuthenticationPrincipal User user,
            @PathVariable int year,
            @PathVariable @Range(min = 1, max = 12) int month
    ) {
        return ResponseEntity.ok(timelineService.getActivityDate(user, year, month));
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
            @Parameter(description = "페이지네이션 정보") Pageable pageable) {
        return ResponseEntity.ok(timelineService.getUserTimelines(user, userId, pageable));
    }

    @Operation(summary = "본인 타임라인 조회", description = "본인의 모든 타임라인을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/me")
    public ResponseEntity<Page<TimelineListResponse>> getSelfTimelines(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "페이지네이션 정보") Pageable pageable) {
        return ResponseEntity.ok(timelineService.getUserTimelines(user, user.getId(), pageable));
    }

    @Operation(summary = "팔로잉 타임라인 조회", description = "팔로우한 사용자들의 타임라인을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/following")
    public ResponseEntity<Page<TimelineListResponse>> getFollowingTimelines(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "페이지네이션 정보") Pageable pageable) {
        return ResponseEntity.ok(timelineService.getFollowingTimelines(user, pageable));
    }

    @Operation(summary = "타임라인 삭제", description = "특정 타임라인을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "타임라인 삭제 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "타임라인을 찾을 수 없음")
    })
    @DeleteMapping("/{timelineId}")
    public ResponseEntity<Void> deleteTimeline(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "삭제할 타임라인 ID") @PathVariable Long timelineId) {
        timelineService.deleteTimeline(user, timelineId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "타임라인 상세 조회", description = "특정 타임라인의 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "타임라인을 찾을 수 없음")
    })
    @GetMapping("/{timelineId}")
    public ResponseEntity<TimelineResponse> getTimelineDetail(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "조회할 타임라인 ID") @PathVariable Long timelineId) {
        return ResponseEntity.ok(timelineService.getTimelineDetail(user, timelineId));
    }

    @Operation(summary = "타임라인 좋아요 토글", description = "타임라인 좋아요를 추가하거나 취소합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "좋아요 토글 성공"),
        @ApiResponse(responseCode = "404", description = "타임라인을 찾을 수 없음")
    })
    @PostMapping("/{timelineId}/likes")
    public ResponseEntity<Boolean> toggleTimelineLike(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "타임라인 ID") @PathVariable Long timelineId) {
        boolean isLiked = timelineService.toggleTimelineLike(user, timelineId);
        return ResponseEntity.ok(isLiked);
    }

}

