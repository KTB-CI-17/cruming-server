package com.ci.Cruming.user.dto;

import com.ci.Cruming.common.constants.Platform;
import com.ci.Cruming.common.constants.UserStatus;
import com.ci.Cruming.location.dto.LocationDTO;
import com.ci.Cruming.user.entity.User;

import java.time.LocalDateTime;

public record UserDTO(
        Long id,
        String nickname,
        Short height,
        Short armReach,
        Platform platform,
        String platformId,
        LocationDTO locationDTO,
        UserStatus status,
        LocalDateTime createdAt,
        LocalDateTime deletedAt
) {
    public static UserDTO of(Long id) {
        return new UserDTO(id, null, null, null, null, null, null, null, null, null);
    }

    public static UserDTO fromEntity(User entity) {
        return new UserDTO(
                entity.getId(),
                entity.getNickname(),
                entity.getHeight(),
                entity.getArmReach(),
                entity.getPlatform(),
                entity.getPlatformId(),
                LocationDTO.fromEntity(entity.getHomeGym()),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getDeletedAt()
        );
    }
}
