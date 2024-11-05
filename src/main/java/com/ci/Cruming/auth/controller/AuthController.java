package com.ci.Cruming.auth.controller;

import com.ci.Cruming.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ci.Cruming.auth.dto.TokenRequest;
import com.ci.Cruming.auth.dto.TokenResponse;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "소셜 로그인 관련 API")
@Validated
@Slf4j
public class AuthController {
    private final AuthService authService;


    @PostMapping("/token")
    @Operation(summary = "소셜 로그인 토큰 교환", description = "소셜 플랫폼의 액세스 토큰을 서비스 토큰으로 교환합니다.")
    public ResponseEntity<TokenResponse> exchangeToken(@Valid @RequestBody TokenRequest request) {
        log.info("Token exchange requested for provider: {}", request.provider());
        return ResponseEntity.ok(authService.exchangeToken(request));
    }
} 