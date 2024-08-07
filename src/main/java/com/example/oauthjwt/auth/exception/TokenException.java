package com.example.oauthjwt.auth.exception;

import com.example.oauthjwt.global.exception.BusinessException;
import com.example.oauthjwt.global.exception.constant.ErrorCode;

public class TokenException extends BusinessException {

    public TokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
