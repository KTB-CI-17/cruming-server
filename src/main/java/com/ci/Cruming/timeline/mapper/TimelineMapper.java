package com.ci.Cruming.timeline.mapper;

import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.timeline.dto.TimelineReplyRequest;
import com.ci.Cruming.timeline.dto.TimelineRequest;
import com.ci.Cruming.timeline.entity.TimelineReply;
import com.ci.Cruming.user.entity.User;
import org.springframework.stereotype.Component;

import com.ci.Cruming.timeline.entity.Timeline;

@Component
public class TimelineMapper {
    public Timeline toEntity(TimelineRequest request, User user, Location location) {
        return Timeline.builder()
            .user(user)
            .location(location)
            .level(request.getLevel())
            .content(request.getContent())
            .visibility(request.getVisibility())
            .activityAt(request.getActivityAt())
            .build();
    }

    public TimelineReply toEntity(TimelineReplyRequest request, Timeline timeline, User user, TimelineReply parent) {
        return TimelineReply.builder()
            .timeline(timeline)
            .parent(parent)
            .user(user)
            .content(request.getContent())
            .build();
    }
} 