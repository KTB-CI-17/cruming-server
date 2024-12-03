package com.ci.Cruming.user.controller;

import com.ci.Cruming.user.dto.UserInfoResponse;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{findUserId}")
    @Operation(summary = "유저 정보 조회", description = "유저의 정보를 조회합니다.")
    public ResponseEntity<UserInfoResponse> findUser(
            @AuthenticationPrincipal User loginUser,
            @PathVariable(required = false) Long findUserId) {

        UserInfoResponse userInfo = userService.findUserInfo(loginUser, findUserId);
        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/")
    @Operation(summary = "유저 정보 조회", description = "유저의 정보를 조회합니다.")
    public ResponseEntity<UserInfoResponse> findMe(
            @AuthenticationPrincipal User loginUser) {

        UserInfoResponse userInfo = userService.findUserInfo(loginUser, null);
        return ResponseEntity.ok(userInfo);
    }


}
