package com.ci.Cruming.timeline.repository;

import com.ci.Cruming.timeline.entity.TimelineReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimelineReplyRepository extends JpaRepository<TimelineReply, Long> {
    Optional<TimelineReply> findByIdAndDeletedAtIsNull(Long id);


    Page<TimelineReply> findByTimelineIdAndParentIsNull(Long timelineId, Pageable pageable);
    Page<TimelineReply> findByParentId(Long timelineId, Pageable pageable);
    Long countByParentId(Long timelineId);
}