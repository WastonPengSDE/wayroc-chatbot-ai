package com.wayroc.wayrocchatbot.exception;

import com.wayroc.wayrocchatbot.common.ErrorCode;

public class BusinessException extends RuntimeException {

    private  final  int code;

    private  final  String message;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();

    }

    public BusinessException(ErrorCode errorCode, String desription) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();

    }


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
