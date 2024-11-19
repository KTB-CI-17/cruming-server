package com.ci.Cruming.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    POST_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "권한이 없는 게시글입니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "올바르지 않은 요청입니다."),
    INVALID_TITLE(HttpStatus.BAD_REQUEST, "제목을 입력해주세요."),
    INVALID_CONTENT(HttpStatus.BAD_REQUEST, "본문을 입력해주세요."),
    INVALID_LEVEL(HttpStatus.BAD_REQUEST, "난이도를 입력해주세요."),
    INVALID_LOCATION(HttpStatus.BAD_REQUEST, "암장 위치를 선택해주세요");

    private HttpStatus status;
    private String message;
}
