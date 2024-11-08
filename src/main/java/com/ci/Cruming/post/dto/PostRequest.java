package com.ci.Cruming.post.dto;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.common.constants.Visibility;
import com.ci.Cruming.user.dto.UserDTO;

public record PostRequest(
        String title,
        String content,
        Category category
        ) {

    public PostDTO toDTO(UserDTO userDTO) {
        return new PostDTO(userDTO, category, title, content);
    }

}
