package com.ci.Cruming.timeline.service.validator;

import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.timeline.dto.TimelineReplyRequest;
import com.ci.Cruming.timeline.entity.TimelineReply;
import com.ci.Cruming.user.entity.User;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class TimelineReplyValidator {

    private static final int MAX_CONTENT_BYTES = 1000 * 3;

    public void validateTimelineReplyRequest(TimelineReplyRequest request) {
        if (request == null) {
            throw new CrumingException(ErrorCode.INVALID_REPLY);
        }

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new CrumingException(ErrorCode.INVALID_REPLY);
        }

        if (request.getContent().getBytes(StandardCharsets.UTF_8).length > MAX_CONTENT_BYTES) {
            throw new CrumingException(ErrorCode.INVALID_REPLY_SIZE);
        }
    }

    public void validateTimelineReplyAuthor(TimelineReply timelineReply, User user) {
        if (!timelineReply.getUser().equals(user)) {
            throw new CrumingException(ErrorCode.TIMELINE_REPLY_NOT_AUTHORIZED);
        }
    }

}
