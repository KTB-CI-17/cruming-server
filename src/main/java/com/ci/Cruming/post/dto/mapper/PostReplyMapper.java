package com.ci.Cruming.post.dto.mapper;

import com.ci.Cruming.common.utils.FileUtils;
import com.ci.Cruming.post.dto.PostReplyResponse;
import com.ci.Cruming.post.dto.PostReplyRequest;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.post.entity.PostReply;
import com.ci.Cruming.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostReplyMapper {

    private final FileUtils fileUtils;

    public PostReplyMapper(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

    public PostReply toPostReply(User user, Post post, PostReply parentReply, PostReplyRequest request) {
        return PostReply.builder()
                .user(user)
                .content(request.content())
                .post(post)
                .parent(parentReply)
                .build();
    }

    public PostReplyResponse toParentPostReplyResponse(User user, PostReply postReply, Long totalChildCount) {
        boolean isWriter = postReply.getUser().getId().equals(user.getId());
        return new PostReplyResponse(
                postReply.getId(),
                postReply.getUser().getId(),
                postReply.getContent(),
                postReply.getCreatedAt(),
                fileUtils.generatePresignedUrl(postReply.getUser().getProfileImageUrl()),
                postReply.getUser().getNickname(),
                isWriter,
                totalChildCount
        );
    }

    public PostReplyResponse toChildPostReplyResponse(User user, PostReply postReply) {
        boolean isWriter = postReply.getUser().getId().equals(user.getId());
        return new PostReplyResponse(
                postReply.getId(),
                postReply.getUser().getId(),
                postReply.getContent(),
                postReply.getCreatedAt(),
                fileUtils.generatePresignedUrl(postReply.getUser().getProfileImageUrl()),
                postReply.getUser().getNickname(),
                isWriter,
                0L
        );
    }
}
