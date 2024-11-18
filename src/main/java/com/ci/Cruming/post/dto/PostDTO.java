package com.ci.Cruming.post.dto;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.common.constants.Visibility;
import com.ci.Cruming.location.dto.LocationDTO;
import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.user.dto.UserDTO;
import com.ci.Cruming.user.entity.User;

import java.time.LocalDateTime;

public record PostDTO(
        Long id,
        UserDTO userDTO,
        LocationDTO locationDTO,
        String level,
        Category category,
        String title,
        String content,
        Visibility visibility,
        LocalDateTime createdAt,
        LocalDateTime deletedAt) {

    public PostDTO(UserDTO userDTO, Category category, String title, String content) {
        this(null, userDTO, null, null, category, title, content, Visibility.PUBLIC, null, null);
    }

    public PostDTO(UserDTO userDTO, LocationDTO locationDTO, String level, Category category, String title, String content) {
        this(null, userDTO, locationDTO, level, category, title, content, Visibility.PUBLIC, null, null);
    }

    public Post toEntity(User user, Location location) {
        return Post.builder()
                .id(id)
                .user(user)
                .location(location)
                .level(level)
                .category(category)
                .title(title)
                .content(content)
                .visibility(visibility)
                .createdAt(createdAt)
                .deletedAt(deletedAt)
                .build();
    }

    public Post toEntity(User user) {
        return Post.builder()
                .id(id)
                .user(user)
                .level(level)
                .category(category)
                .title(title)
                .content(content)
                .visibility(visibility)
                .createdAt(createdAt)
                .deletedAt(deletedAt)
                .build();
    }

    public static PostDTO fromEntity(Post entity) {
        return new PostDTO(
                entity.getId(),
                UserDTO.fromEntity(entity.getUser()),
                null,
//                LocationDTO.fromEntity(entity.getLocation()),
                entity.getLevel(),
                entity.getCategory(),
                entity.getTitle(),
                entity.getContent(),
                entity.getVisibility(),
                entity.getCreatedAt(),
                entity.getDeletedAt()
        );
    }
}