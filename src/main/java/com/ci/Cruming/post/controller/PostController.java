package com.ci.Cruming.post.controller;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.post.dto.*;
import com.ci.Cruming.post.service.PostService;
import com.ci.Cruming.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "게시글 API", description = "게시글 CRUD API")
public class PostController {

    private final PostService postService;

    @Operation(
            summary = "게시글 작성",
            description = "새로운 게시글을 작성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "게시글 작성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PostMapping
    public ResponseEntity<Void> createPost(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "게시글 정보") @RequestPart(value = "request") PostRequest request,
            @Parameter(description = "첨부 파일 목록") @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        postService.createPost(user, request, files);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/edit/{postId}")
    @Operation(
            summary = "게시글 수정을 위한 데이터 조회",
            description = "게시글 수정에 필요한 기존 게시글 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    public ResponseEntity<PostEditInfo> editPost(
            @Parameter(description = "수정할 게시글 ID") @PathVariable Long postId) {
        PostEditInfo postEditInfo = postService.findPostEditInfo(postId);
        return ResponseEntity.ok(postEditInfo);
    }

    @PostMapping("/{postId}")
    @Operation(
            summary = "게시글 수정",
            description = "기존 게시글을 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    public ResponseEntity<Void> updatePost(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "수정할 게시글 ID") @PathVariable Long postId,
            @Parameter(description = "수정할 게시글 정보") @RequestPart(value = "request") PostEditRequest request,
            @Parameter(description = "수정할 첨부 파일 목록") @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        postService.updatePost(user, postId, request, files);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}")
    @Operation(
            summary = "게시글 삭제",
            description = "게시글을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "삭제할 게시글 ID") @PathVariable Long postId) {
        postService.deletePost(user, postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(
            summary = "커뮤니티 게시글 리스트 조회",
            description = "게시판의 종류를 입력받고 페이징 처리된 게시글 리스트를 전달합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<Page<PostListResponse>> findPostList(
            @Parameter(description = "페이징 정보") Pageable pageable,
            @Parameter(description = "게시판 카테고리") @RequestParam Category category) {
        return ResponseEntity.ok(postService.findPostList(pageable, category));
    }

    @GetMapping("/{postId}")
    @Operation(
            summary = "게시글 상세 조회",
            description = "게시글 상세 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    public ResponseEntity<PostResponse> findPost(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal User user,
            @Parameter(description = "조회할 게시글 ID") @PathVariable Long postId) {
        PostResponse postResponse = postService.findPost(user, postId);
        return ResponseEntity.ok().body(postResponse);
    }

    @PostMapping("/{postId}/views")
    @Operation(
            summary = "게시글 조회수 증가",
            description = "게시글의 조회수를 1 증가 시킵니다. (프론트에서 무한 새로고침으로 조회수 증가 방지)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "조회수 증가 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    public ResponseEntity<Void> increasePostView(
            @Parameter(description = "조회수를 증가시킬 게시글 ID") @PathVariable Long postId) {
        postService.increasePostView(postId);
        return ResponseEntity.noContent().build();
    }
}

