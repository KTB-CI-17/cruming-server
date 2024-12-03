package com.ci.Cruming.post.dto.mapper;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.common.constants.Visibility;
import com.ci.Cruming.file.dto.FileResponse;
import com.ci.Cruming.file.entity.File;
import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.post.dto.*;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.user.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class PostMapper {

    public Post toGeneralPost(User user, PostGeneralRequest request) {
        return Post.builder()
                .user(user)
                .title(request.title())
                .content(request.content())
                .category(Category.GENERAL)
                .visibility(Visibility.PUBLIC)
                .views(0L)
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
                .views(0L)
                .build();
    }

    public PostListResponse toPostListResponse(Post post) {
        return new PostListResponse(
                post.getId(),
                post.getTitle(),
                post.getCreatedAt()
        );
    }

    public PostResponse toPostResponse(User user, Post post, List<FileResponse> files, boolean isLiked, Long likeCount, Long replyCount) {
        boolean isWriter = user.getId().equals(post.getUser().getId());
        String location = null;
        if (post.getLocation() != null) {
            location = post.getLocation().getPlaceName();
        }

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                location,
                post.getLevel(),
                post.getCategory(),
                post.getVisibility(),
                post.getCreatedAt(),
                post.getUser().getId(),
                post.getUser().getNickname(),
                null, // TODO: user profile 가져오도록 수정
                post.getUser().getInstagramId(),
                isWriter,
                files,
                isLiked,
                likeCount,
                replyCount,
                post.getViews()
        );
    }

    public PostEditInfo toPostEditInfo(Post post, List<FileResponse> files) {
        return PostEditInfo.builder()
                .id(post.getId())
                .category(post.getCategory())
                .title(post.getTitle())
                .content(post.getContent())
                .location(post.getLocation().getPlaceName())
                .level(post.getLevel())
                .files(files)
                .build();
    }

}
