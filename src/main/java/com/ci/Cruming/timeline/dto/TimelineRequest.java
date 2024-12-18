package com.ci.Cruming.timeline.dto;

import java.time.LocalDateTime;

import com.ci.Cruming.common.constants.Visibility;

import com.ci.Cruming.location.dto.LocationRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TimelineRequest {
    @NotNull
    LocationRequest location;
    
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