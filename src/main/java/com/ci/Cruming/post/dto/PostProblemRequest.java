package com.ci.Cruming.post.dto;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.location.dto.LocationRequest;
import com.ci.Cruming.user.dto.UserDTO;

public record PostProblemRequest(
        String title,
        String content,
        LocationRequest location,
        String level
) {
    public PostDTO toDTO(UserDTO userDTO) {
        return new PostDTO(userDTO, location.toDTO(), level, Category.PROBLEM, title, content);
    }
}

