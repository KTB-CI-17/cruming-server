package com.ci.Cruming.timeline.dto;

import lombok.Builder;

@Builder
public record TimelineUserDTO(
        Long id,
        String nickname,
        String profileImageUrl
) {
}
