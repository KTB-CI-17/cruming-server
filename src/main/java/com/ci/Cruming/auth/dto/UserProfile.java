package com.ci.Cruming.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class UserProfile {
    @NotBlank(message = "Nickname is required")
    private final String nickname;
    
    @NotBlank(message = "platformId is required")
    private final String platformId;
    
    private final String image;
} 