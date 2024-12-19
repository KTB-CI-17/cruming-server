package com.ci.Cruming.timeline.dto.mapper;

import com.ci.Cruming.common.utils.FileUtils;
import com.ci.Cruming.timeline.dto.TimelineReplyRequest;
import com.ci.Cruming.timeline.dto.TimelineReplyResponse;
import com.ci.Cruming.timeline.entity.Timeline;
import com.ci.Cruming.timeline.entity.TimelineReply;
import com.ci.Cruming.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TimelineReplyMapper {

    private final FileUtils fileUtils;

    public TimelineReplyResponse toParentTimelineReplyResponse(User user, TimelineReply timelineReply, Long totalChildCount) {
        boolean isWriter = timelineReply.getUser().getId().equals(user.getId());
        return new TimelineReplyResponse(
                timelineReply.getId(),
                timelineReply.getUser().getId(),
                timelineReply.getContent(),
                timelineReply.getCreatedAt(),
                fileUtils.generatePresignedUrl(timelineReply.getUser().getProfileImageUrl()),
                timelineReply.getUser().getNickname(),
                isWriter,
                totalChildCount
        );
    }

    public TimelineReplyResponse toChildTimelineReplyResponse(User user, TimelineReply timelineReply) {
        boolean isWriter = timelineReply.getUser().getId().equals(user.getId());
        return new TimelineReplyResponse(
                timelineReply.getId(),
                timelineReply.getUser().getId(),
                timelineReply.getContent(),
                timelineReply.getCreatedAt(),
                fileUtils.generatePresignedUrl(timelineReply.getUser().getProfileImageUrl()),
                timelineReply.getUser().getNickname(),
                isWriter,
                0L
        );
    }

    public TimelineReply toTimelineReply(User user, Timeline timeline, TimelineReply parentReply, TimelineReplyRequest request) {
        return TimelineReply.builder()
                .user(user)
                .content(request.getContent())
                .timeline(timeline)
                .parent(parentReply)
                .build();
    }
}
