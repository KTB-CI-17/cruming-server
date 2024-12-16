package com.ci.Cruming.user.dto;

import lombok.Builder;

@Builder
public record UserInfoResponse(
        Long id,
        String nickname,
        String profile,
        Short height,
        Short armReach,
        String intro,
        String homeGym,
        String instagramId,
        Long followingCount,
        Long followerCount,
        boolean isMe,
        boolean isFollowing,
        boolean isFollowingMe
) {
}
