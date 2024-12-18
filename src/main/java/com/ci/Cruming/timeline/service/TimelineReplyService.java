package com.ci.Cruming.timeline.service;

import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.timeline.dto.TimelineReplyRequest;
import com.ci.Cruming.timeline.dto.TimelineReplyResponse;
import com.ci.Cruming.timeline.dto.mapper.TimelineReplyMapper;
import com.ci.Cruming.timeline.entity.Timeline;
import com.ci.Cruming.timeline.entity.TimelineReply;
import com.ci.Cruming.timeline.dto.mapper.TimelineMapper;
import com.ci.Cruming.timeline.repository.TimelineReplyRepository;
import com.ci.Cruming.timeline.repository.TimelineRepository;
import com.ci.Cruming.timeline.service.validator.TimelineReplyValidator;
import com.ci.Cruming.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimelineReplyService {

    private final TimelineRepository timelineRepository;
    private final TimelineReplyRepository timelineReplyRepository;
    private final TimelineReplyValidator timelineReplyValidator;
    private final TimelineReplyMapper timelineReplyMapper;

    @Transactional
    public void createReply(User user, Long timelineId, TimelineReplyRequest request) {
        timelineReplyValidator.validateTimelineReplyRequest(request);

        Timeline timeline = timelineRepository.findById(timelineId)
            .orElseThrow(() -> new CrumingException(ErrorCode.TIMELINE_NOT_FOUND));

        TimelineReply parent = request.getParentId() != null
            ? timelineReplyRepository.findByIdAndDeletedAtIsNull(request.getParentId())
                .orElseThrow(() -> new CrumingException(ErrorCode.REPLY_NOT_FOUND))
            : null;

        TimelineReply reply = timelineReplyMapper.toTimelineReply(user, timeline, parent, request);
        timelineReplyRepository.save(reply);
    }

    @Transactional
    public void updateTimelineReply(User user, TimelineReplyRequest request, Long replyId) {
        TimelineReply reply = timelineReplyRepository.findById(replyId)
                .orElseThrow(() -> new CrumingException(ErrorCode.REPLY_NOT_FOUND));
        timelineReplyValidator.validateTimelineReplyAuthor(reply, user);
        timelineReplyValidator.validateTimelineReplyRequest(request);

        reply.update(request.getContent());
    }

    @Transactional
    public void deleteTimelineReply(User user, Long replyId) {
        TimelineReply reply = timelineReplyRepository.findById(replyId)
                .orElseThrow(() -> new CrumingException(ErrorCode.REPLY_NOT_FOUND));
        timelineReplyValidator.validateTimelineReplyAuthor(reply, user);

        timelineReplyRepository.delete(reply);
    }

    public Page<TimelineReplyResponse> findTimelineReplyList(User user, Pageable pageable, Long timelineId) {
        Page<TimelineReply> parentReplies = timelineReplyRepository.findByTimelineIdAndParentIsNull(timelineId, pageable);
        List<TimelineReplyResponse> timelineReplyResponses = parentReplies.getContent()
                .stream()
                .map(parent -> timelineReplyMapper.toParentTimelineReplyResponse(
                        user,
                        parent,
                        timelineReplyRepository.countByParentId(parent.getId())
                ))
                .toList();

        return new PageImpl<>(timelineReplyResponses, pageable, parentReplies.getTotalElements());
    }

    public Page<TimelineReplyResponse> findTimelineChildReplyList(User user, Long parentId, Pageable pageable) {
        return timelineReplyRepository.findByParentId(parentId, pageable)
                .map(timelineReply -> timelineReplyMapper.toChildTimelineReplyResponse(user, timelineReply));
    }

    @Transactional
    public void createTimelineReply(User user, TimelineReplyRequest request, Long timelineId, Long parentId) {
        Timeline timeline = timelineRepository.findById(timelineId)
                .orElseThrow(() -> new CrumingException(ErrorCode.TIMELINE_NOT_FOUND));
        TimelineReply parentReply = validateAndGetParentReply(parentId, timelineId);

        timelineReplyValidator.validateTimelineReplyRequest(request);
        TimelineReply reply = timelineReplyMapper.toTimelineReply(user, timeline, parentReply, request);

        timelineReplyRepository.save(reply);
    }

    private TimelineReply validateAndGetParentReply(Long parentId, Long timelineId) {
        if (parentId == null || parentId == 0) {
            return null;
        }

        TimelineReply parentReply = timelineReplyRepository.findById(parentId)
                .orElseThrow(() -> new CrumingException(ErrorCode.REPLY_NOT_FOUND));

        if (!parentReply.getTimeline().getId().equals(timelineId)) {
            throw new CrumingException(ErrorCode.INVALID_REPLY_AND_TIMELINE);
        }

        return parentReply;
    }

}
