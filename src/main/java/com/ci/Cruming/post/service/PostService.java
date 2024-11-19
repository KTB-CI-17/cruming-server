package com.ci.Cruming.post.service;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.location.service.LocationService;
import com.ci.Cruming.post.dto.PostListResponse;
import com.ci.Cruming.post.dto.PostProblemRequest;
import com.ci.Cruming.post.dto.PostGeneralRequest;
import com.ci.Cruming.post.dto.mapper.PostMapper;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.post.service.validator.PostValidator;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.post.repository.PostRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final LocationService locationService;
    private final PostValidator postValidator;
    private final PostMapper postMapper;

    @Transactional
    public void createGeneral(User user, PostGeneralRequest request) {
        postValidator.validatePostGeneralRequest(request);
        Post post = postMapper.toGeneralPost(user, request);
        postRepository.save(post);
    }

    @Transactional
    public void createProblem(User user, PostProblemRequest request) {
        postValidator.validatePostProblemRequest(request);
        Location location = locationService.getOrCreateLocation(request.location());
        Post post = postMapper.toProblemPost(user, request, location);

        postRepository.save(post);
    }

    @Transactional
    public void updateGeneral(User user, Long postId, PostGeneralRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CrumingException(ErrorCode.POST_NOT_FOUND));
        postValidator.validatePostAuthor(post, user);
        postValidator.validatePostGeneralRequest(request);

        post.update(request.title(), request.content());
    }

    @Transactional
    public void updateProblem(User user, Long postId, PostProblemRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CrumingException(ErrorCode.POST_NOT_FOUND));
        postValidator.validatePostAuthor(post, user);
        postValidator.validatePostProblemRequest(request);

        Location location = locationService.getOrCreateLocation(request.location());
        post.update(request.title(), request.content(), request.level(), location);
    }

    public Page<PostListResponse> findPostList(Pageable pageable, Category category) {
        return postRepository.findByPostInCategory(pageable, category)
                .map(postMapper::toPostListResponse);
    }
}
