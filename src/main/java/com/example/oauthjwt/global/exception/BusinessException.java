package com.example.oauthjwt.global.exception;

import com.example.oauthjwt.global.exception.constant.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private ErrorCode errorCode;
    private String message;

    public BusinessException() {
        super();
        this.errorCode = ErrorCode.UNKNOWN;
    }

    public BusinessException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.message = message;
    }
}
