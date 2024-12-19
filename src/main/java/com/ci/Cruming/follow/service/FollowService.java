package com.ci.Cruming.follow.service;

import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.follow.dto.FollowUserResponse;
import com.ci.Cruming.follow.entity.Follow;
import com.ci.Cruming.follow.repository.FollowRepository;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public Boolean toggleFollow(User loginUser, Long targetUserId) {
        if (loginUser.getId().equals(targetUserId)) {
            throw new CrumingException(ErrorCode.CANNOT_FOLLOW_SELF);
        }

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new CrumingException(ErrorCode.USER_NOT_FOUND));

        if (followRepository.existsByFollowerAndFollowing(target, loginUser)) {
            followRepository.deleteByFollowerAndFollowing(target, loginUser);
            return false;
        }
        Follow follow = Follow.builder()
                .follower(target)
                .following(loginUser)
                .build();
        followRepository.save(follow);
        return true;
    }

    public Page<FollowUserResponse> getFollowers(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CrumingException(ErrorCode.USER_NOT_FOUND));

        return followRepository.findByFollower(user, pageable)
                .map(follow -> new FollowUserResponse(
                        follow.getFollowing().getId(),
                        follow.getFollowing().getNickname(),
                        follow.getFollowing().getProfileImageUrl(),
                        follow.getFollowing().getInstagramId()
                ));
    }

    public Page<FollowUserResponse> getFollowings(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CrumingException(ErrorCode.USER_NOT_FOUND));

        return followRepository.findByFollowing(user, pageable)
                .map(follow -> new FollowUserResponse(
                        follow.getFollower().getId(),
                        follow.getFollower().getNickname(),
                        follow.getFollower().getProfileImageUrl(),
                        follow.getFollower().getInstagramId()
                ));
    }

    @Transactional(readOnly = true)
    public List<Long> getAllFollowingIds(Long userId) {
        return followRepository.findByFollowerId(userId).stream()
            .map(follow -> follow.getFollowing().getId())
            .toList();
    }
}