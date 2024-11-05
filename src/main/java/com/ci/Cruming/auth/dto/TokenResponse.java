package com.ci.Cruming.auth.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record TokenResponse(
    @NotBlank
    String accessToken,
    @NotBlank
    String refreshToken,

    LocalDateTime expiresAt
) {} 