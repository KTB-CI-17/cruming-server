package com.ci.Cruming.user.dto;

import com.ci.Cruming.location.dto.LocationRequest;

public record UserEditRequest(
        String nickname,
        Short height,
        Short armReach,
        String intro,
        String instagramId,
        LocationRequest homeGymRequest
) {
}
