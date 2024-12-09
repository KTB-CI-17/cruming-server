package com.ci.Cruming.timeline.repository;

import com.ci.Cruming.timeline.entity.Timeline;
import com.ci.Cruming.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimelineRepository extends JpaRepository<Timeline, Long> {
    List<Timeline> findByUserOrderByCreatedAtDesc(User user);
    Optional<Timeline> findByIdAndDeletedAtIsNull(Long id);
    List<Timeline> findByUserAndActivityAtBetweenOrderByActivityAtDesc(
        User user, LocalDateTime startDateTime, LocalDateTime endDateTime);
    Page<Timeline> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    Page<Timeline> findByUserAndActivityAtBetweenOrderByActivityAtDesc(
        User user, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);
    Page<Timeline> findByUserAndCreatedAtBetweenOrderByCreatedAtDesc(
        User user, 
        LocalDateTime startDateTime, 
        LocalDateTime endDateTime, 
        Pageable pageable
    );
    Page<Timeline> findByUserIdInOrderByCreatedAtDesc(List<Long> userIds, Pageable pageable);
} 