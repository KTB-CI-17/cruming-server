package com.ci.Cruming.user.dto;

import com.ci.Cruming.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserInfoResponse toUserInfoResponse(
            User findUser,
            User loginUser,
            Long followingCount,
            Long followerCount,
            boolean isFollowing,
            boolean isFollowingMe
    ) {
        boolean isMe = findUser.getId().equals(loginUser.getId());

        return UserInfoResponse.builder()
                .id(findUser.getId())
                .nickname(findUser.getNickname())
                .profile(null) // TODO: user profile image
                .height(findUser.getHeight())
                .armReach(findUser.getArmReach())
                .intro(findUser.getIntro())
                .homeGym(findUser.getHomeGymPlaceName())
                .instagramId(findUser.getInstagramId())
                .followingCount(followingCount)
                .followerCount(followerCount)
                .isMe(isMe)
                .isFollowing(isFollowing)
                .isFollowingMe(isFollowingMe)
                .build();
    }
}
