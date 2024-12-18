package com.ci.Cruming.timeline.service.validator;

import org.springframework.stereotype.Component;

import com.ci.Cruming.timeline.dto.TimelineRequest;
import com.ci.Cruming.timeline.entity.Timeline;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TimelineValidator {
    
    public void validateTimelineRequest(TimelineRequest request) {
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new CrumingException(ErrorCode.INVALID_REQUEST, "Content cannot be empty");
        }
        if (request.getContent().length() > 3000) {
            throw new CrumingException(ErrorCode.INVALID_REQUEST, "Content length exceeds maximum limit");
        }
        if (request.getLevel() == null || request.getLevel().trim().isEmpty()) {
            throw new CrumingException(ErrorCode.INVALID_REQUEST, "Level cannot be empty");
        }
        if (request.getLevel().length() > 20) {
            throw new CrumingException(ErrorCode.INVALID_REQUEST, "Level length exceeds maximum limit");
        }
    }
    
    public void validateTimelineAuthor(Timeline timeline, User user) {
        if (!timeline.getUser().getId().equals(user.getId())) {
            throw new CrumingException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }
}