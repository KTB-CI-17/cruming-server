package com.ci.Cruming.post.controller;

import com.ci.Cruming.post.dto.PostReplyResponse;
import com.ci.Cruming.post.dto.PostReplyRequest;
import com.ci.Cruming.post.service.PostReplyService;
import com.ci.Cruming.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostReplyController {

    private final PostReplyService postReplyService;

    @PostMapping("/{postId}/replies")
    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다.")
    public ResponseEntity<Void> createTopLevelReply(
            @AuthenticationPrincipal User user,
            @PathVariable Long postId,
            @RequestBody PostReplyRequest request
    ) {
        postReplyService.createPostReply(user, request, postId, null);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/replies/{parentId}")
    @Operation(summary = "대댓글 작성", description = "게시글에 대댓글을 작성합니다.")
    public ResponseEntity<Void> createReply(
            @AuthenticationPrincipal User user,
            @PathVariable Long postId,
            @PathVariable Long parentId,
            @RequestBody PostReplyRequest request
    ) {
        postReplyService.createPostReply(user, request, postId, parentId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/replies/{replyId}")
    @Operation(summary = "댓글 수정", description = "게시글에 댓글을 수정합니다.")
    public ResponseEntity<Void> updateReply(
            @AuthenticationPrincipal User user,
            @PathVariable Long replyId,
            @RequestBody PostReplyRequest request
    ) {
        postReplyService.updatePostReply(user, request, replyId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/replies/{replyId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    public ResponseEntity<Void> deleteReply(
            @AuthenticationPrincipal User user,
            @PathVariable Long replyId
    ) {
        postReplyService.deletePostReply(user, replyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/replies")
    @Operation(summary = "댓글 조회", description = "댓글을 조회합니다.")
    public ResponseEntity<Page<PostReplyResponse>> getPostReplies(
            @AuthenticationPrincipal User user,
            @PathVariable Long postId,
            Pageable pageable) {
        return ResponseEntity.ok(postReplyService.findPostReplyList(user, pageable, postId));
    }

    @GetMapping("/replies/{parentId}/children")
    public ResponseEntity<Page<PostReplyResponse>> getChildReplies(
            @AuthenticationPrincipal User user,
            @PathVariable Long parentId,
            Pageable pageable) {
        return ResponseEntity.ok(postReplyService.findPostChildReplyList(user, parentId, pageable));
    }


}
