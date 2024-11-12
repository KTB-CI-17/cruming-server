package com.ci.Cruming.post.service;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.location.dto.LocationDTO;
import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.location.repository.LocationRepository;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.post.dto.PostDTO;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.user.repository.UserRepository;
import com.ci.Cruming.post.repository.PostLikeRepository;
import com.ci.Cruming.post.repository.PostReplyRepository;
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
@Transactional
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostReplyRepository postReplyRepository;
    private final LocationRepository locationRepository;

    @Transactional(readOnly = false)
    public Long savePost(PostDTO postDTO) {
        User user = userRepository.getReferenceById(postDTO.userDTO().id());
        Post post = postDTO.toEntity(user);
        log.info("post={}", post.toString());
        postRepository.save(post);

        return post.getId();
    }

    @Transactional(readOnly = false)
    public Long savePostProblem(PostDTO postDTO) {
        User user = userRepository.getReferenceById(postDTO.userDTO().id());
        Location location = getOrCreateLocation(postDTO.locationDTO());
        Post post = postDTO.toEntity(user, location);
        postRepository.save(post);
        return post.getId();
    }

    public Page<PostDTO> findPostList(Pageable pageable, Category category) {
        return postRepository.findByCategoryOrderByCreatedAtDesc(pageable, category).map(PostDTO::fromEntity);
    }

    private Location getOrCreateLocation(LocationDTO locationDTO) {
        return locationRepository
                .findByPlaceNameAndAddress(locationDTO.placeName(), locationDTO.address())
                .orElseGet(() -> {
                    Location newLocation = locationDTO.toEntity();
                    return locationRepository.save(newLocation);
                });
    }

}
