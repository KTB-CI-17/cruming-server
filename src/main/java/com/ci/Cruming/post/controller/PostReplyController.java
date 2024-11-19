package com.ci.Cruming.post.controller;

import com.ci.Cruming.post.dto.PostReplyRequest;
import com.ci.Cruming.post.service.PostReplyService;
import com.ci.Cruming.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostReplyController {

    private final PostReplyService postReplyService;

    @PostMapping("/{postId}/replies/{parentId}")
    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다.")
    public ResponseEntity<Void> createReply(
            @AuthenticationPrincipal User user,
            @PathVariable Long postId,
            @PathVariable(required = false) Long parentId,
            @RequestBody PostReplyRequest request
    ) {
        postReplyService.createPostReply(user, request, postId, parentId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/replies/{replyId}")
    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다.")
    public ResponseEntity<Void> updateReply(
            @AuthenticationPrincipal User user,
            @PathVariable Long replyId,
            @RequestBody PostReplyRequest request
    ) {
        postReplyService.updatePostReply(user, request, replyId);
        return ResponseEntity.ok().build();
    }



}
