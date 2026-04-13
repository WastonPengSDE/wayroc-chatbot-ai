package com.wayroc.wayrocchatbot.common;

public enum ErrorCode {

    SUCCESS(0, "ok", ""),
    PARAM_ERROR(40000, "parameter error", ""),
    Null_ERROR(40001, "parameter is null", ""),
    NOT_LOGIN(40100, "user is not login", ""),
    NO_AUTH(40300, "not authorized", ""),
    RATE_LIMIT(42900, "请求过于频繁，请稍后再试", ""),
    SYSTEM_ERROR(50000, "system error", "");



    private int code;

    private final  String message;

    private final  String description;






    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }

}
