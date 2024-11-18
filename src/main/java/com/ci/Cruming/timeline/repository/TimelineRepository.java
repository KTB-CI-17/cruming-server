package com.ci.Cruming.timeline.repository;

import com.ci.Cruming.timeline.entity.Timeline;
import com.ci.Cruming.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimelineRepository extends JpaRepository<Timeline, Long> {
    List<Timeline> findByUserOrderByCreatedAtDesc(User user);
    Optional<Timeline> findByIdAndDeletedAtIsNull(Long id);
} 