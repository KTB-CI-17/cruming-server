package com.ci.Cruming.post.service;

import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.post.dto.PostReplyResponse;
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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostReplyService {

    private final PostRepository postRepository;
    private final PostReplyRepository postReplyRepository;
    private final PostReplyValidator postReplyValidator;
    private final PostReplyMapper postReplyMapper;
    private static final int CHILD_REPLY_SIZE = 5;

    @Transactional
    public void createPostReply(User user, PostReplyRequest request, Long postId, Long parentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CrumingException(ErrorCode.POST_NOT_FOUND));
        PostReply parentReply = validateAndGetParentReply(parentId, postId);

        postReplyValidator.validatePostReplyRequest(request);
        PostReply reply = postReplyMapper.toPostReply(user, post, parentReply, request);

        postReplyRepository.save(reply);
    }

    @Transactional
    public void updatePostReply(User user, PostReplyRequest request, Long replyId) {
        PostReply reply = postReplyRepository.findById(replyId)
                .orElseThrow(() -> new CrumingException(ErrorCode.REPLY_NOT_FOUND));
        postReplyValidator.validatePostReplyAuthor(reply, user);
        postReplyValidator.validatePostReplyRequest(request);

        reply.update(request.content());
    }

    @Transactional
    public void deletePostReply(User user, Long replyId) {
        PostReply reply = postReplyRepository.findById(replyId)
                .orElseThrow(() -> new CrumingException(ErrorCode.REPLY_NOT_FOUND));
        postReplyValidator.validatePostReplyAuthor(reply, user);

        postReplyRepository.delete(reply);
    }

    public Page<PostReplyResponse> findPostReplyList(User user, Pageable pageable, Long postId) {
        Page<PostReply> parentReplies = postReplyRepository.findByPostIdAndParentIsNull(postId, pageable);

        List<PostReplyResponse> result = parentReplies.getContent().stream()
                .map(parent -> {
                    Page<PostReply> children = postReplyRepository.findByParentId(
                            parent.getId(),
                            PageRequest.of(0, CHILD_REPLY_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"))
                    );

                    List<PostReplyResponse> childResponses = children.getContent().stream()
                            .map(postReply -> postReplyMapper.toChildPostReplyResponse(user, postReply))
                            .toList();

                    return postReplyMapper.toParentPostReplyResponse(
                            user,
                            parent,
                            childResponses,
                            children.getTotalElements()
                    );
                })
                .toList();

        return new PageImpl<>(result, pageable, parentReplies.getTotalElements());
    }

    public Page<PostReplyResponse> findPostChildReplyList(User user, Long parentId, Pageable pageable) {
        return postReplyRepository.findByParentId(parentId, pageable)
                .map(postReply -> postReplyMapper.toChildPostReplyResponse(user, postReply));
    }

    private PostReply validateAndGetParentReply(Long parentId, Long postId) {
        if (parentId == null || parentId == 0) {
            return null;
        }

        PostReply parentReply = postReplyRepository.findById(parentId)
                .orElseThrow(() -> new CrumingException(ErrorCode.REPLY_NOT_FOUND));

        if (!parentReply.getPost().getId().equals(postId)) {
            throw new CrumingException(ErrorCode.INVALID_REPLY_AND_POST);
        }

        return parentReply;
    }

}