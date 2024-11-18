package com.ci.Cruming.timeline.repository;

import com.ci.Cruming.timeline.entity.Timeline;
import com.ci.Cruming.timeline.entity.TimelineLike;
import com.ci.Cruming.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimelineLikeRepository extends JpaRepository<TimelineLike, Long> {
    Optional<TimelineLike> findByTimelineAndUser(Timeline timeline, User user);
    boolean existsByTimelineAndUser(Timeline timeline, User user);
} 