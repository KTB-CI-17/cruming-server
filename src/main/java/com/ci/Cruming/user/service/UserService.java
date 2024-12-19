package com.ci.Cruming.user.service;

import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.file.service.FileService;
import com.ci.Cruming.follow.repository.FollowRepository;
import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.location.service.LocationService;
import com.ci.Cruming.post.dto.PostEditRequest;
import com.ci.Cruming.user.dto.UserEditInfo;
import com.ci.Cruming.user.dto.UserEditRequest;
import com.ci.Cruming.user.dto.UserInfoResponse;
import com.ci.Cruming.user.dto.UserMapper;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.user.repository.UserRepository;
import com.ci.Cruming.user.service.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final FileService fileService;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final LocationService locationService;

    public UserInfoResponse findUserInfo(User loginUser, Long findUserId) {
        Long targetId = (findUserId == null) ? loginUser.getId() : findUserId;

        User findUser = userRepository.findByIdWithHomeGym(targetId)
                .orElseThrow(() -> new CrumingException(ErrorCode.USER_NOT_FOUND));

        boolean isMe = loginUser.getId().equals(findUser.getId());
        Long followingCount = followRepository.countByFollowing(findUser);
        Long followerCount = followRepository.countByFollower(findUser);

        boolean isFollowing = false;
        boolean isFollowingMe = false;

        if (!isMe) {
            isFollowing = followRepository.existsByFollowerAndFollowing(findUser, loginUser);
            isFollowingMe = followRepository.existsByFollowerAndFollowing(loginUser, findUser);
        }

        return userMapper.toUserInfoResponse(
                findUser,
                loginUser,
                followingCount,
                followerCount,
                isFollowing,
                isFollowingMe
        );
    }

    public UserEditInfo findUserEditInfo(Long userId) {
        return userMapper.toUserEditInfo(
                userRepository.findByIdWithHomeGym(userId)
                        .orElseThrow(() -> new CrumingException(ErrorCode.USER_NOT_FOUND))
        );
    }

    @Transactional
    public void updateProfileImageUrl(MultipartFile newProfileImage, User user) {
        String fileUrl = fileService.storeProfileImageAndGetFileKey(newProfileImage);
        user.setProfileImageUrl(fileUrl);
        userRepository.save(user);
    }

    @Transactional
    public void updateProfile(User user, UserEditRequest request) {
        userValidator.validateUserEditRequest(request);
        user.update(request.nickname(), request.height(), request.armReach(), request.intro(), updateLocation(request), request.instagramId());
        userRepository.save(user);
    }

    private Location updateLocation(UserEditRequest request) {
        if (request.homeGymRequest() == null) {
            return null;
        }
        return locationService.getOrCreateLocation(request.homeGymRequest());
    }
}
