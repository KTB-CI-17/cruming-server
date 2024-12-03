package com.ci.Cruming.user.service;

import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.follow.repository.FollowRepository;
import com.ci.Cruming.user.dto.UserInfoResponse;
import com.ci.Cruming.user.dto.UserMapper;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final UserMapper userMapper;

    public UserInfoResponse findUserInfo(User loginUser, Long findUserId) {
        User findUser;

        if (findUserId == null) {
            findUser = loginUser;
        } else {
            findUser = userRepository.findById(findUserId)
                    .orElseThrow(() -> new CrumingException(ErrorCode.USER_NOT_FOUND));
        }

        Long followingCount = followRepository.countByFollowing(findUser);
        Long followerCount = followRepository.countByFollower(findUser);

        return userMapper.toUserInfoResponse(findUser, loginUser, followingCount, followerCount);
    }
}
