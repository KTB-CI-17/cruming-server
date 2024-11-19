package com.ci.Cruming.post.service;

import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.post.dto.PostReplyRequest;
import com.ci.Cruming.post.dto.mapper.PostReplyMapper;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.post.entity.PostReply;
import com.ci.Cruming.post.repository.PostReplyRepository;
import com.ci.Cruming.post.repository.PostRepository;
import com.ci.Cruming.post.service.validator.PostReplyValidator;
import com.ci.Cruming.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostReplyService {

    private final PostRepository postRepository;
    private final PostReplyRepository postReplyRepository;
    private final PostReplyValidator postReplyValidator;
    private final PostReplyMapper postReplyMapper;

    @Transactional
    public void createPostReply(User user, PostReplyRequest request, Long postId, Long parentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CrumingException(ErrorCode.POST_NOT_FOUND));

        postReplyValidator.validatePostReplyRequest(request);

        PostReply parentReply = validateAndGetParentReply(parentId, postId);
        PostReply reply = postReplyMapper.toPostReply(user, post, parentReply, request);

        postReplyRepository.save(reply);
    }

    private PostReply validateAndGetParentReply(Long parentId, Long postId) {
        if (parentId == null) {
            return null;
        }

        PostReply parentReply = postReplyRepository.findById(parentId)
                .orElseThrow(() -> new CrumingException(ErrorCode.REPLY_NOT_FOUND));

        if (!parentReply.getPost().getId().equals(postId)) {
            throw new CrumingException(ErrorCode.INVALID_PARENT_REPLY_AND_POST);
        }

        return parentReply;
    }
}