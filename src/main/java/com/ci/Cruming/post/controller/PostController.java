package com.ci.Cruming.post.controller;

import com.ci.Cruming.post.dto.PostProblemRequest;
import com.ci.Cruming.user.dto.UserDTO;
import com.ci.Cruming.post.dto.PostRequest;
import com.ci.Cruming.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Long> savePost(@RequestBody PostRequest postRequest) {
        log.info(postRequest.toString());
        Long postId = postService.savePost(postRequest.toDTO(createUserDTO()));

        return ResponseEntity.ok().body(postId);
    }

    private UserDTO createUserDTO() {
        return UserDTO.of(1L);
    }

    @PostMapping("/problems")
    public ResponseEntity<Long> saveProblem(@RequestBody PostProblemRequest postProblemRequest) {
        log.info(postProblemRequest.toString());
        Long postId = postService.savePostProblem(postProblemRequest.toDTO(createUserDTO()));

        return ResponseEntity.ok().body(postId);
    }

}
