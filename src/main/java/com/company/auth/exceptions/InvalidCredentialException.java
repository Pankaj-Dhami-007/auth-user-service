package com.company.auth.exceptions;

import com.company.auth.enums.ResponseErrorCodes;
import lombok.Getter;

@Getter
public class InvalidCredentialException extends RuntimeException{

    private ResponseErrorCodes responseErrorCodes;
    public InvalidCredentialException(ResponseErrorCodes r){
        super(r.getErrorMsg());
        this.responseErrorCodes = r;
    }
}
