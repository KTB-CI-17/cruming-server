package com.ci.Cruming.post.dto.mapper;

import com.ci.Cruming.post.dto.PostReplyRequest;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.post.entity.PostReply;
import com.ci.Cruming.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PostReplyMapper {
    public PostReply toPostReply(User user, Post post, PostReply parentReply, PostReplyRequest request) {
        return PostReply.builder()
                .user(user)
                .content(request.content())
                .post(post)
                .parent(parentReply)
                .build();
    }
}
