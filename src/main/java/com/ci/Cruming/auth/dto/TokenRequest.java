package com.ci.Cruming.auth.dto;

import jakarta.validation.constraints.NotBlank;


public record TokenRequest(
    @NotBlank(message = "Social token is required")
    String socialToken,

    @NotBlank(message = "Provider is required")
    String provider
) {} 