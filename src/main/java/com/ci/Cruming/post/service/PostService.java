package com.ci.Cruming.post.service;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.location.service.LocationService;
import com.ci.Cruming.post.dto.PostListResponse;
import com.ci.Cruming.post.dto.PostProblemRequest;
import com.ci.Cruming.post.dto.PostGeneralRequest;
import com.ci.Cruming.post.dto.mapper.PostMapper;
import com.ci.Cruming.post.entity.Post;
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
    private final PostMapper postMapper;

    @Transactional
    public void createPost(User user, PostGeneralRequest postGeneralRequest) {
        Post post = postMapper.toGeneralPost(user, postGeneralRequest);

        postRepository.save(post);
    }

    @Transactional
    public void createProblems(User user, PostProblemRequest request) {
        Location location = locationService.getOrCreateLocation(request.location());
        Post post = postMapper.toProblemPost(user, request, location);

        postRepository.save(post);
    }

    public Page<PostListResponse> findPostList(Pageable pageable, Category category) {
        return postRepository.findByPostInCategory(pageable, category)
                .map(postMapper::toPostListResponse);
    }

}
