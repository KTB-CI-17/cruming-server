package com.ci.Cruming.post.controller;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.post.dto.*;
import com.ci.Cruming.post.service.PostService;
import com.ci.Cruming.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "게시글 작성")
    public ResponseEntity<Void> createPost(
            @AuthenticationPrincipal User user,
            @RequestPart(value = "locationRequest") PostRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        postService.createPost(user, request, files);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/edit/{postId}")
    @Operation(summary = "게시글 수정을 위한 데이터 조회")
    public ResponseEntity<PostEditInfo> editPost(
            @PathVariable Long postId) {
        PostEditInfo postEditInfo = postService.findPostEditInfo(postId);
        return ResponseEntity.ok(postEditInfo);
    }

    @PostMapping("/{postId}")
    @Operation(summary = "게시글 수정")
    public ResponseEntity<Void> updatePost(
            @AuthenticationPrincipal User user,
            @PathVariable Long postId,
            @RequestPart(value = "request") PostEditRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        postService.updatePost(user, postId, request, files);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    public ResponseEntity<Void> deletePost(
            @AuthenticationPrincipal User user,
            @PathVariable Long postId) {
        postService.deletePost(user, postId);
        return ResponseEntity.ok().build();
    }

    // TODO: 최신순, 인기순 필터 기능 추가
    @GetMapping
    @Operation(summary = "커뮤니티 게시글 리스트 조회", description = "게시판의 종류를 입력받고 페이징 처리된 게시글 리스트를 전달합니다.")
    public ResponseEntity<Page<PostListResponse>> findPostList(Pageable pageable, @RequestParam Category category) {
        return ResponseEntity.ok(postService.findPostList(pageable, category));
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시글 상세 조회", description = "게시글 상세 정보를 조회합니다.")
    public ResponseEntity<PostResponse> findPost(@AuthenticationPrincipal User user, @PathVariable Long postId) {
        PostResponse postResponse = postService.findPost(user, postId);
        return ResponseEntity.ok().body(postResponse);
    }

    @PostMapping("/{postId}/views")
    @Operation(summary = "게시글 조회수 증가", description = "게시글의 조회수를 1 증가 시킵니다. (프론트에서 무한 새로고침으로 조회수 증가 방지)")
    public ResponseEntity<Void> increasePostView(@PathVariable Long postId) {
        postService.increasePostView(postId);
        return ResponseEntity.noContent().build();
    }

}
