package com.example.oauthjwt.domain.user.exception;

import com.example.oauthjwt.global.exception.BusinessException;
import com.example.oauthjwt.global.exception.constant.ErrorCode;

public class UserException extends BusinessException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
