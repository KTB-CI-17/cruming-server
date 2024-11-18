package com.ci.Cruming.timeline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TimelineReplyRequest {
    private Long parentId;
    
    @NotBlank
    @Size(max = 1500)
    private String content;
} 