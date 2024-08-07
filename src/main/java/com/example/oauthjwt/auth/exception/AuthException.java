package com.example.oauthjwt.auth.exception;


import com.example.oauthjwt.global.exception.BusinessException;
import com.example.oauthjwt.global.exception.constant.ErrorCode;

public class AuthException extends BusinessException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
