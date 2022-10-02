package com.minswap.hrms.constants;

import org.springframework.http.HttpStatus;

import com.minswap.hrms.model.BusinessCode;

public class ErrorCode {
    public static final BusinessCode INTERNAL_SERVER_ERROR = new BusinessCode(
            "INTERNAL_SERVER_ERROR", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final BusinessCode THIRD_PARTY_ERROR = new BusinessCode(
            "THIRD_PARTY_ERROR", "Third party error", HttpStatus.INTERNAL_SERVER_ERROR);

    public static final BusinessCode INVALID_PARAMETERS = new BusinessCode(
            "INVALID_PARAMETERS", "Invalid parameters", HttpStatus.BAD_REQUEST);
    public static final BusinessCode UNAUTHORIZED = new BusinessCode(
            "UNAUTHORIZED", "Unauthorized", HttpStatus.UNAUTHORIZED);

    public static final BusinessCode RESOURCE_NOT_FOUND = new BusinessCode(
            "RESOURCE_NOT_FOUND", "Resource not found", HttpStatus.NOT_FOUND);

    public static BusinessCode newErrorCode(String message) {
        return new BusinessCode("INTERNAL_SERVER_ERROR", message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static BusinessCode newErrorCode(String code,String message) {
      return new BusinessCode(code, message, null);
    }

    public static BusinessCode newErrorCode(String code,String message, HttpStatus httpStatus) {
      return new BusinessCode(code, message, httpStatus);
    }
    private ErrorCode() {
    }
}
