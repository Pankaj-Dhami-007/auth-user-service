package com.company.auth.enums;

import lombok.Getter;

@Getter
public enum ResponseErrorCodes {

    USER_NOT_FOUND(1001, "User not found"),
    INVALID_CREDENTIALS(1002, "Invalid credentials provided"),
    INVALID_REFRESH_TOKEN(1003,""),
    INVALID_CLIENT_FOUND(1004,"Client not valid");

    private int code;
    private String errorMsg;

    ResponseErrorCodes(int code, String errorMsg) {
        this.code = code;
        this.errorMsg = errorMsg;
    }
}
