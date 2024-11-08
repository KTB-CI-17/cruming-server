package com.ci.Cruming.user.dto;

import com.ci.Cruming.common.constants.Platform;
import com.ci.Cruming.common.constants.UserStatus;
import com.ci.Cruming.location.dto.LocationDTO;

import java.time.LocalDateTime;

public record UserDTO(
        Long id,
        String nickname,
        Integer height,
        Integer armReach,
        Platform platform,
        Long platformId,
        LocationDTO locationDTO,
        UserStatus status,
        LocalDateTime createdAt,
        LocalDateTime deletedAt
) {
    public static UserDTO of(Long id) {
        return new UserDTO(id, null, null, null, null, null, null, null, null, null);
    }
}
