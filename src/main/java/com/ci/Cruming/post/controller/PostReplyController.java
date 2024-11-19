package com.ci.Cruming.post.controller;

import com.ci.Cruming.post.dto.PostReplyRequest;
import com.ci.Cruming.post.service.PostReplyService;
import com.ci.Cruming.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts/{postId}/replies")
@RequiredArgsConstructor
public class PostReplyController {

    private final PostReplyService postReplyService;

    @PostMapping("/{replyId}")
    public ResponseEntity<Void> createReply(
            @AuthenticationPrincipal User user,
            @PathVariable Long postId,
            @PathVariable(required = false) Long replyId,
            @RequestBody PostReplyRequest request
    ) {
        postReplyService.createPostReply(user, request, postId, replyId);
        return ResponseEntity.ok().build();
    }
}
