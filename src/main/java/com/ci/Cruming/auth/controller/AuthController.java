package com.ci.Cruming.auth.controller;

import com.ci.Cruming.auth.dto.AccessTokenResponse;
import com.ci.Cruming.auth.service.AuthService;

import com.ci.Cruming.auth.service.JwtTokenProvider;
import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ci.Cruming.auth.dto.TokenRequest;
import com.ci.Cruming.auth.dto.TokenResponse;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "소셜 로그인 관련 API")
@Validated
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/code")
    @Operation(summary = "카카오 로그인 코드로 소셜 로그인 토큰 교환")
    public ResponseEntity<AccessTokenResponse> getToken(String code, HttpServletResponse response) {
        TokenRequest tokenRequest = authService.kakaoLoginAPI(code);
        TokenResponse tokenResponse = authService.exchangeToken(tokenRequest);

        setSecureCookies(response, tokenResponse);

        return ResponseEntity.ok(new AccessTokenResponse(tokenResponse.accessToken()));
    }

    private void setSecureCookies(HttpServletResponse response, TokenResponse tokenResponse) {
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenResponse.refreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();

        ResponseCookie expiresAtCookie = ResponseCookie.from("expiresAt", tokenResponse.expiresAt().toString())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expiresAtCookie.toString());
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        log.info("재발급 요청");

        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new CrumingException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        TokenResponse tokenResponse = authService.refreshToken(refreshToken);
        setSecureCookies(response, tokenResponse);

        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/auth/token")
    @Operation(summary = "소셜 로그인 토큰 교환", description = "소셜 플랫폼의 액세스 토큰을 서비스 토큰으로 교환합니다.")
    public ResponseEntity<TokenResponse> exchangeToken(@Valid @RequestBody TokenRequest request,
                                                       HttpServletResponse response) {
        TokenResponse tokenResponse = authService.exchangeToken(request);
        addRefreshTokenCookie(response, tokenResponse.refreshToken());
        return ResponseEntity.ok(tokenResponse);
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        throw new CrumingException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
} 