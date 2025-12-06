package com.wayroc.wayrocchatbot.common;

public enum ErrorCode {

    SUCCESS(0, "ok", ""),
    PARMAS_ERROR(40000, "paramater error",""),
    Null_ERROR(40001,"paramater is null",""),
    NOT_LOGIN(40000, "user is not login",""),
    NO_AUTH(40000, "not authorized",""),
    SYSTEM_ERROR(40000, "system error","");



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
