package com.ci.Cruming.post.dto.mapper;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.common.constants.Visibility;
import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.post.dto.PostListResponse;
import com.ci.Cruming.post.dto.PostProblemRequest;
import com.ci.Cruming.post.dto.PostGeneralRequest;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public Post toGeneralPost(User user, PostGeneralRequest request) {
        return Post.builder()
                .user(user)
                .title(request.title())
                .content(request.content())
                .category(Category.GENERAL)
                .build();
    }

    public Post toProblemPost(User user, PostProblemRequest request, Location location) {
        return Post.builder()
                .user(user)
                .location(location)
                .level(request.level())
                .category(Category.PROBLEM)
                .title(request.title())
                .content(request.content())
                .visibility(Visibility.PUBLIC)
                .build();
    }

    public PostListResponse toPostListResponse(Post post) {
        return new PostListResponse(
                post.getId(),
                post.getTitle(),
                post.getCreatedAt()
        );
    }

}
