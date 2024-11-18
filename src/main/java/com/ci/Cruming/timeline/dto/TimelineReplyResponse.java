package com.ci.Cruming.timeline.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ci.Cruming.user.dto.UserDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TimelineReplyResponse(
    @NotNull
    Long id,
    
    @NotNull
    UserDTO userDTO,
    
    @NotBlank
    @Size(max = 1500)
    String content,
    
    List<TimelineReplyResponse> children,
    
    @NotNull
    LocalDateTime createdAt,
    
    LocalDateTime updatedAt
) {}