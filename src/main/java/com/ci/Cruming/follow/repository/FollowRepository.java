package com.ci.Cruming.follow.repository;

import com.ci.Cruming.follow.entity.Follow;
import com.ci.Cruming.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Long countByFollower(User user);
    Long countByFollowing(User user);
}
