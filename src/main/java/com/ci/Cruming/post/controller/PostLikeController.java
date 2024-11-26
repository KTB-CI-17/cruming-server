package com.ci.Cruming.post.controller;

import com.ci.Cruming.post.service.PostLikeService;
import com.ci.Cruming.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts/{postId}/likes")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping
    @Operation(summary = "게시글 좋아요 및 좋아요 취소", description = "게시글에 좋아요를 합니다. 혹은 좋아요를 취소합니다.")
    public ResponseEntity<Boolean> toggleLike(
            @AuthenticationPrincipal User user,
            @PathVariable Long postId
    ) {
        boolean isLiked = postLikeService.likeOrUnlike(user, postId);
        return ResponseEntity.ok().body(isLiked);
    }


}
