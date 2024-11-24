package com.ci.Cruming.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "에러가 발생하였습니다. 계속 될 경우 관리자에게 문의해주세요."),

    POST_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "권한이 없는 게시글입니다."),
    POST_REPLY_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "권한이 없는 댓글입니다."),

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),
    REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."),

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "올바르지 않은 요청입니다."),
    INVALID_TITLE(HttpStatus.BAD_REQUEST, "제목을 입력해주세요."),
    INVALID_CONTENT(HttpStatus.BAD_REQUEST, "본문을 입력해주세요."),
    INVALID_LEVEL(HttpStatus.BAD_REQUEST, "난이도를 입력해주세요."),
    INVALID_LOCATION(HttpStatus.BAD_REQUEST, "암장 위치를 선택해주세요"),
    INVALID_REPLY(HttpStatus.BAD_REQUEST, "댓글을 입력해주세요."),
    INVALID_REPLY_SIZE(HttpStatus.BAD_REQUEST, "댓글은 최대 500자 까지 입력 가능합니다."),
    INVALID_REPLY_AND_POST(HttpStatus.BAD_REQUEST, "작성할 수 없는 댓글입니다."),
    INVALID_POST_CONTENT_SIZE(HttpStatus.BAD_REQUEST, "본문은 최대 1,000자 까지 입력 가능합니다."),
    INVALID_POST_TITLE_SIZE(HttpStatus.BAD_REQUEST, "제목은 최대 100자 까지 입력 가능합니다."),
    INVALID_POST_LEVEL_SIZE(HttpStatus.BAD_REQUEST, "난이도는 최대 50자 까지 입력 가능합니다."),

    FAIL_FILE_SAVE(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패하였습니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "잘못된 파일 이름입니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다."),
    FILE_SIZE_EXCEED(HttpStatus.BAD_REQUEST, "파일 크기가 제한을 초과했습니다."),
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "파일이 비어있습니다."),
    FAIL_FILE_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),
    FILE_NAME_MISMATCH(HttpStatus.BAD_REQUEST, "파일명이 일치하지 않습니다."),
    INVALID_FILE_REQUEST(HttpStatus.BAD_REQUEST, "파일 요청 정보가 올바르지 않습니다."),
    MAX_FILE_COUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "최대 5개까지만 업로드할 수 있습니다."),
    ;

    private HttpStatus status;
    private String message;
}
