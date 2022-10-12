package com.minswap.hrms.constants;

import org.springframework.http.HttpStatus;

import com.minswap.hrms.model.BusinessCode;

public class ErrorCode {
    public static final BusinessCode INTERNAL_SERVER_ERROR = new BusinessCode(
    		ErrorCodeEnum.INTERNAL_SERVER_ERROR.getValue(), "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

    public static final BusinessCode INVALID_PARAMETERS = new BusinessCode(
    		ErrorCodeEnum.BAD_REQUEST.getValue(), "Invalid parameters", HttpStatus.BAD_REQUEST);
    public static final BusinessCode UNAUTHORIZED = new BusinessCode(
    		ErrorCodeEnum.UNAUTHORIZED.getValue(), "Unauthorized", HttpStatus.UNAUTHORIZED);


    public static BusinessCode newErrorCode(String message) {
        return new BusinessCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR.getValue(), message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static BusinessCode newErrorCode(int code,String message) {
      return new BusinessCode(code, message, null);
    }

    public static BusinessCode newErrorCode(int code,String message, HttpStatus httpStatus) {
      return new BusinessCode(code, message, httpStatus);
    }
    private ErrorCode() {
    }
}
