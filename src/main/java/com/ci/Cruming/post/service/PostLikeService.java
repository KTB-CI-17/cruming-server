package com.ci.Cruming.post.service;

import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.post.entity.PostLike;
import com.ci.Cruming.post.repository.PostLikeRepository;
import com.ci.Cruming.post.repository.PostRepository;
import com.ci.Cruming.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public boolean likeOrUnlike(User user, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CrumingException(ErrorCode.POST_NOT_FOUND));

        Optional<PostLike> existingLike = postLikeRepository.findByPostAndUser(post, user);

        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            return false;
        } else {
            PostLike postLike = PostLike.builder()
                    .user(user)
                    .post(post)
                    .build();
            postLikeRepository.save(postLike);
            return true;
        }
    }
}
