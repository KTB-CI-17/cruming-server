package com.ci.Cruming.timeline.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ci.Cruming.common.constants.Visibility;
import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.user.dto.UserDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;


public record TimelineResponse (
    @NotNull
    Long id,
    
    @NotNull
    UserDTO userDTO,
    
    @NotNull
    Location location,
    
    @NotBlank
    @Size(max = 20)
    String level,
    
    @NotBlank
    @Size(max = 3000)
    String content,
    
    @NotNull
    Visibility visibility,
    
    @NotNull
    LocalDateTime activityAt,
    
    @PositiveOrZero
    int likeCount,
    
    @PositiveOrZero
    int replyCount,
    
    boolean isLiked,
    
    List<TimelineReplyResponse> replies,
    
    @NotNull
    LocalDateTime createdAt
){}
