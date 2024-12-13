package com.ci.Cruming.user.dto;

import com.ci.Cruming.common.utils.FileUtils;
import com.ci.Cruming.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final FileUtils fileUtils;

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
                .profile(fileUtils.generatePresignedUrl(findUser.getProfileImageUrl()))
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

    public UserEditInfo toUserEditInfo(User user) {
        return UserEditInfo.builder()
                .profileImageUrl(fileUtils.generatePresignedUrl(user.getProfileImageUrl()))
                .nickname(user.getNickname())
                .height(user.getHeight())
                .armReach(user.getArmReach())
                .intro(user.getIntro())
                .instagramId(user.getInstagramId())
                .homeGym(Optional.ofNullable(user.getHomeGym())
                        .map(gym -> new UserEditInfo.HomeGym(
                                gym.getPlaceName(),
                                gym.getAddress(),
                                gym.getLatitude(),
                                gym.getLongitude()
                        ))
                        .orElse(null))
                .build();
    }
}
