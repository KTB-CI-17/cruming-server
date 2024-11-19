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
    INVALID_POST_LEVEL_SIZE(HttpStatus.BAD_REQUEST, "난이도는 최대 50자 까지 입력 가능합니다.");

    private HttpStatus status;
    private String message;
}
