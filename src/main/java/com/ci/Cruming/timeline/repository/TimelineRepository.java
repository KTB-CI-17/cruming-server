package com.ci.Cruming.timeline.repository;

import com.ci.Cruming.timeline.entity.Timeline;
import com.ci.Cruming.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimelineRepository extends JpaRepository<Timeline, Long> {

    Page<Timeline> findByUserAndActivityAtBetweenOrderByActivityAtDesc(User user, LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query("SELECT DISTINCT t.activityAt FROM Timeline t " +
            "WHERE t.activityAt BETWEEN :startDate AND :endDate " +
            "AND t.user = :user " +
            "ORDER BY t.activityAt")
    List<LocalDate> findDistinctActivityAtByUserBetween(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    Page<Timeline> findByUserOrderByActivityAtDesc(User user, Pageable pageable);

    @Query("SELECT t FROM Timeline t " +
            "JOIN Follow f ON t.user = f.following " +
            "WHERE f.follower = :user AND t.visibility <> 'private'")
    Page<Timeline> findTimelinesByFollowerId(@Param("user") User user, Pageable pageable);
}