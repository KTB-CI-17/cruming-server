package com.ci.Cruming.post.controller;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.post.dto.PostDTO;
import com.ci.Cruming.post.dto.PostListResponse;
import com.ci.Cruming.post.dto.PostProblemRequest;
import com.ci.Cruming.user.dto.UserDTO;
import com.ci.Cruming.post.dto.PostRequest;
import com.ci.Cruming.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "자유게시판 게시글 작성", description = "자유게시판 게시글을 저장합니다.")
    public ResponseEntity<Long> savePost(@RequestBody PostRequest postRequest) {
        log.info(postRequest.toString());
        Long postId = postService.savePost(postRequest.toDTO(createUserDTO()));

        return ResponseEntity.ok().body(postId);
    }

    private UserDTO createUserDTO() {
        return UserDTO.of(1L);
    }

    @PostMapping("/problems")
    @Operation(summary = "만든 문제 게시글 작성", description = "만든 문제 게시글을 저장합니다.")
    public ResponseEntity<Long> saveProblem(@RequestBody PostProblemRequest postProblemRequest) {
        log.info(postProblemRequest.toString());
        Long postId = postService.savePostProblem(postProblemRequest.toDTO(createUserDTO()));

        return ResponseEntity.ok().body(postId);
    }

    // TODO: 최신순, 인기순 필터 기능 추가
    @GetMapping
    @Operation(summary = "커뮤니티 게시글 리스트 조회", description = "게시판의 종류를 입력받고 페이징 처리된 게시글 리스트를 전달합니다.")
    public ResponseEntity<Page<PostListResponse>> findPostList(Pageable pageable, @RequestParam Category category) {
        log.info("Category={}", category.toString());
        Page<PostDTO> postList = postService.findPostList(pageable, category);
        Page<PostListResponse> response = postList.map(PostListResponse::from);

        return ResponseEntity.ok(response);
    }
}
