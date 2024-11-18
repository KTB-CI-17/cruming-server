package com.ci.Cruming.timeline.dto;

import java.time.LocalDateTime;

import com.ci.Cruming.common.constants.Visibility;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TimelineRequest {
    @NotNull
    private Long locationId;
    
    @NotBlank
    @Size(max = 20)
    private String level;
    
    @NotBlank
    @Size(max = 3000)
    private String content;
    
    @NotNull
    private Visibility visibility;
    
    @NotNull
    private LocalDateTime activityAt;
}