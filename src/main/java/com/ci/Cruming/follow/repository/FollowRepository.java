package com.ci.Cruming.follow.repository;

import com.ci.Cruming.follow.entity.Follow;
import com.ci.Cruming.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Long countByFollower(User user);
    Long countByFollowing(User user);
    boolean existsByFollowerAndFollowing(User follower, User following);
    Page<Follow> findByFollower(User follower, Pageable pageable);
    Page<Follow> findByFollowing(User following, Pageable pageable);
    List<Follow> findByFollowerId(Long followerId);
    @Modifying
    @Query("DELETE FROM Follow f WHERE f.follower = :follower AND f.following = :following")
    void deleteByFollowerAndFollowing(@Param("follower") User follower, @Param("following") User following);

}
