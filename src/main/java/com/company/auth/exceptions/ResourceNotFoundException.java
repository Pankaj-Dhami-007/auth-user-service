package com.company.auth.exceptions;

import com.company.auth.enums.ResponseErrorCodes;
import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException{
   private ResponseErrorCodes responseErrorCodes;
   public ResourceNotFoundException(ResponseErrorCodes responseErrorCodes){
        super(responseErrorCodes.getErrorMsg());
        this.responseErrorCodes = responseErrorCodes;
    }
}
