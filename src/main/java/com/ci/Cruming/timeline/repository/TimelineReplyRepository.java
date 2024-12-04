package com.ci.Cruming.timeline.repository;

import com.ci.Cruming.timeline.entity.Timeline;
import com.ci.Cruming.timeline.entity.TimelineReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimelineReplyRepository extends JpaRepository<TimelineReply, Long> {
    List<TimelineReply> findAllByTimelineAndDeletedAtIsNull(Timeline timeline);
    List<TimelineReply> findByTimelineAndParentIsNullOrderByCreatedAtAsc(Timeline timeline);
    Optional<TimelineReply> findByIdAndDeletedAtIsNull(Long id);
    Page<TimelineReply> findByTimelineAndParentIsNullOrderByCreatedAtAsc(Timeline timeline, Pageable pageable);
} 