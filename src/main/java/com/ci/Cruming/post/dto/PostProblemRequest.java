package com.ci.Cruming.post.dto;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.location.dto.LocationDTO;
import com.ci.Cruming.user.dto.UserDTO;

public record PostProblemRequest(
        String title,
        String content,
        String placeName,
        String address,
        Double latitude,
        Double longitude,
        String level
) {

}
