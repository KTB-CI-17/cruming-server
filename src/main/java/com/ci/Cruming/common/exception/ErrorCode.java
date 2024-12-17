package com.ci.Cruming.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Authentication & Authorization
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "로그인이 만료되었습니다. 다시 로그인해 주세요."),
    FAIL_GET_KAKAO_ACCESS_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 인증 토큰 발급에 실패했습니다."),
    POST_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "해당 게시글에 대한 권한이 없습니다."),
    POST_REPLY_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "해당 댓글에 대한 권한이 없습니다."),

    // Resource Not Found
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),

    // Post Validation
    INVALID_TITLE(HttpStatus.BAD_REQUEST, "게시글 제목을 입력해 주세요."),
    INVALID_CONTENT(HttpStatus.BAD_REQUEST, "게시글 내용을 입력해 주세요."),
    INVALID_LEVEL(HttpStatus.BAD_REQUEST, "문제 난이도를 입력해 주세요."),
    INVALID_POST_TITLE_SIZE(HttpStatus.BAD_REQUEST, "제목은 100자를 초과할 수 없습니다."),
    INVALID_POST_CONTENT_SIZE(HttpStatus.BAD_REQUEST, "본문은 1,000자를 초과할 수 없습니다."),
    INVALID_POST_LEVEL_SIZE(HttpStatus.BAD_REQUEST, "난이도는 50자를 초과할 수 없습니다."),

    // Reply Validation
    INVALID_REPLY(HttpStatus.BAD_REQUEST, "댓글 내용을 입력해 주세요."),
    INVALID_REPLY_SIZE(HttpStatus.BAD_REQUEST, "댓글은 500자를 초과할 수 없습니다."),
    INVALID_REPLY_AND_POST(HttpStatus.BAD_REQUEST, "유효하지 않은 댓글입니다."),

    // Location Validation
    INVALID_LOCATION(HttpStatus.BAD_REQUEST, "암장 위치를 선택해 주세요."),
    INVALID_LOCATION_ADDRESS(HttpStatus.BAD_REQUEST, "암장 주소 정보가 없습니다. 다시 선택해 주세요."),
    INVALID_LOCATION_PLACE_NAME(HttpStatus.BAD_REQUEST, "암장 이름 정보가 없습니다. 다시 선택해 주세요."),
    INVALID_LOCATION_COORDINATES(HttpStatus.BAD_REQUEST, "암장 좌표 정보가 없습니다. 다시 선택해 주세요."),

    // File Handling
    INVALID_PROBLEM_POST_FILE(HttpStatus.BAD_REQUEST, "문제 사진이 필요합니다."),
    INVALID_EDIT_PROBLEM_POST_FILE(HttpStatus.BAD_REQUEST, "문제 사진은 수정할 수 없습니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "파일 이름이 유효하지 않습니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "지원되지 않는 파일 형식입니다."),
    INVALID_FILE_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 파일 정보입니다."),
    FILE_SIZE_EXCEED(HttpStatus.BAD_REQUEST, "파일 크기가 제한을 초과했습니다."),
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "빈 파일은 업로드할 수 없습니다."),
    PROBLEM_FILE_SIZE_OVER(HttpStatus.BAD_REQUEST, "문제 게시판에는 사진 1장만 업로드할 수 있습니다."),
    MAX_FILE_COUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "최대 5개 파일까지만 업로드할 수 있습니다."),
    FILE_NAME_MISMATCH(HttpStatus.BAD_REQUEST, "파일명이 일치하지 않습니다."),
    FAIL_FILE_SAVE(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패했습니다."),
    FAIL_FILE_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다."),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    FILE_DOWNLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드에 실패했습니다."),
    FILE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "파일에 접근할 수 없습니다."),

    // General
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다. 문제가 지속되면 관리자에게 문의해 주세요."),
    CANNOT_FOLLOW_SELF(HttpStatus.BAD_REQUEST, "자기 자신을 팔로우할 수 없습니다."),
    ALREADY_FOLLOWING(HttpStatus.BAD_REQUEST, "이미 팔로우하고 있는 유저입니다."),
    NOT_FOLLOWING(HttpStatus.BAD_REQUEST, "팔로우하고 있지 않은 유저입니다."),

    // Timeline related
    TIMELINE_NOT_FOUND(HttpStatus.NOT_FOUND, "타임라인을 찾을 수 없습니다."),
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "위치 정보를 찾을 수 없습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "해당 작업에 대한 권한이 없습니다."),

    // user validation
    INVALID_USER_NICKNAME(HttpStatus.BAD_REQUEST, "닉네임을 입력해주세요."),
    INVALID_USER_NICKNAME_SIZE(HttpStatus.BAD_REQUEST, "닉네임은 50글자를 초과할 수 없습니다."),
    INVALID_USER_HEIGHT_SIZE(HttpStatus.BAD_REQUEST, "키를 확인 후 다시 입력해주세요."),
    INVALID_USER_ARM_REACH_SIZE(HttpStatus.BAD_REQUEST, "팔 길이를 확인 후 다시 입력해주세요."),
    INVALID_USER_INTRO_SIZE(HttpStatus.BAD_REQUEST, "한줄 소개는 300자를 초과할 수 없습니다."),
    INVALID_USER_INSTAGRAM_ID_SIZE(HttpStatus.BAD_REQUEST, "인스타그램 ID는 100자를 초과할 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String message;
}