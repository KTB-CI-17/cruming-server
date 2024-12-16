package com.ci.Cruming.follow.controller;

import com.ci.Cruming.follow.dto.FollowUserResponse;
import com.ci.Cruming.follow.service.FollowService;
import com.ci.Cruming.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/follows")
@RequiredArgsConstructor
@Tag(name = "Follow", description = "팔로우 관련 API")
public class FollowController {

    private final FollowService followService;

    @PatchMapping("/toggle/{targetUserId}")
    @Operation(
            summary = "팔로우 토글",
            description = "팔로우를 토글합니다. 팔로우 상태면 언팔로우, 언팔로우 상태면 팔로우로 처리합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "토글 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "자기 자신을 팔로우할 수 없음"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "대상 유저를 찾을 수 없음"
            )
    })
    public ResponseEntity<Boolean> toggleFollow(
            @Parameter(description = "현재 로그인한 사용자", hidden = true)
            @AuthenticationPrincipal User loginUser,
            @Parameter(description = "팔로우를 토글할 사용자 ID")
            @PathVariable Long targetUserId) {
        return ResponseEntity.ok(followService.toggleFollow(loginUser, targetUserId));
    }

    @GetMapping("/followers/{userId}")
    @Operation(
        summary = "팔로워 목록 조회",
        description = "특정 유저의 팔로워 목록을 페이징하여 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "팔로워 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "유저를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = Error.class))
        )
    })
    public ResponseEntity<Page<FollowUserResponse>> getFollowers(
            @Parameter(description = "조회할 사용자 ID")
            @PathVariable Long userId,
            @Parameter(description = "페이지네이션 정보")
            Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowers(userId, pageable));
    }

    @GetMapping("/followings/{userId}")
    @Operation(
        summary = "팔로잉 목록 조회",
        description = "특정 유저의 팔로잉 목록을 페이징하여 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "팔로잉 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "유저를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = Error.class))
        )
    })
    public ResponseEntity<Page<FollowUserResponse>> getFollowings(
            @Parameter(description = "조회할 사용자 ID")
            @PathVariable Long userId,
            @Parameter(description = "페이지네이션 정보")
            Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowings(userId, pageable));
    }
}