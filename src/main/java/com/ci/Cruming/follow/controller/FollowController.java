package com.ci.Cruming.follow.controller;

import com.ci.Cruming.follow.dto.FollowUserResponse;
import com.ci.Cruming.follow.service.FollowService;
import com.ci.Cruming.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}")
    @Operation(summary = "팔로우", description = "특정 유저를 팔로우합니다.")
    public ResponseEntity<Void> follow(
            @AuthenticationPrincipal User user,
            @PathVariable Long userId) {
        followService.follow(user, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "언팔로우", description = "특정 유저를 언팔로우합니다.")
    public ResponseEntity<Void> unfollow(
            @AuthenticationPrincipal User user,
            @PathVariable Long userId) {
        followService.unfollow(user, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/followers/{userId}")
    @Operation(summary = "팔로워 목록 조회", description = "특정 유저의 팔로워 목록을 조회합니다.")
    public ResponseEntity<Page<FollowUserResponse>> getFollowers(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowers(userId, pageable));
    }

    @GetMapping("/followings/{userId}")
    @Operation(summary = "팔로잉 목록 조회", description = "특정 유저의 팔로잉 목록을 조회합니다.")
    public ResponseEntity<Page<FollowUserResponse>> getFollowings(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowings(userId, pageable));
    }
} 