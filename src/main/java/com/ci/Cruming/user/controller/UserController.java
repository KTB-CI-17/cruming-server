package com.ci.Cruming.user.controller;

import com.ci.Cruming.user.dto.UserEditInfo;
import com.ci.Cruming.user.dto.UserEditRequest;
import com.ci.Cruming.user.dto.UserInfoResponse;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Operation(
            summary = "내 정보 수정을 위한 데이터 조회",
            description = "내 정보 수정에 필요한 기존 내 정보를 조회합니다.")
    @GetMapping("/edit")
    public ResponseEntity<UserEditInfo> editUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.findUserEditInfo(user));
    }

    @Operation(
            summary = "프로필 이미지 수정",
            description = "프로필의 이미지를 수정합니다.")
    @PutMapping("/profile-image")
    public ResponseEntity<Void> editProfileImage(@AuthenticationPrincipal User user,
                                                 MultipartFile newProfileImage) {
        userService.updateProfileImageUrl(newProfileImage, user);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "내 정보 수정",
            description = "내 정보를 수정합니다.")
    @PutMapping("/edit")
    public ResponseEntity<Void> editUser(@AuthenticationPrincipal User user,
                                         UserEditRequest request) {
        log.info("request={}", request);
        return ResponseEntity.ok().build();
    }


}
