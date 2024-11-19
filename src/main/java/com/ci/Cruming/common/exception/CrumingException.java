package com.ci.Cruming.common.exception;

import lombok.Getter;

@Getter
public class CrumingException extends RuntimeException {
    private final ErrorCode errorCode;

    public CrumingException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CrumingException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
