package com.ci.Cruming.post.service;

import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.post.dto.PostDTO;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.user.repository.UserRepository;
import com.ci.Cruming.post.repository.PostLikeRepository;
import com.ci.Cruming.post.repository.PostReplyRepository;
import com.ci.Cruming.post.repository.PostRepository;

import lombok.extern.slf4j.Slf4j;
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

    @Transactional(readOnly = false)
    public Long savePost(PostDTO postDTO) {
        User user = userRepository.getReferenceById(postDTO.userDTO().id());
        Post post = postDTO.toEntity(user);
        log.info("post={}", post.toString());
        postRepository.save(post);

        return post.getId();
    }


}
