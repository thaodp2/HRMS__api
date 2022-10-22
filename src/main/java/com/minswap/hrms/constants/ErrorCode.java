package com.minswap.hrms.constants;

import org.springframework.http.HttpStatus;

import com.minswap.hrms.model.BusinessCode;

public class ErrorCode {
    public static final BusinessCode INTERNAL_SERVER_ERROR = new BusinessCode(
    		ErrorCodeEnum.INTERNAL_SERVER_ERROR.getValue(), "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

    public static final BusinessCode DATE_INVALID = new BusinessCode(
            ErrorCodeEnum.DATE_INVALID.getValue(), "CreateDate must be before StartTime", HttpStatus.BAD_REQUEST);

    public static final BusinessCode RESULT_NOT_FOUND = new BusinessCode(
            ErrorCodeEnum.RESULT_NOT_FOUND.getValue(), "Result not found", HttpStatus.NOT_FOUND);

    public static final BusinessCode UPDATE_FAIL = new BusinessCode(
            ErrorCodeEnum.UPDATE_FAIL.getValue(), "Update failed", HttpStatus.NOT_FOUND);

    public static final BusinessCode REQUEST_INVALID = new BusinessCode(
            ErrorCodeEnum.REQUEST_INVALID.getValue(), "You can't cancel an approved or rejected request ",
            HttpStatus.NOT_ACCEPTABLE);

    public static final BusinessCode DELETE_FAIL = new BusinessCode(
            ErrorCodeEnum.DELETE_FAIL.getValue(), "Delete failed", HttpStatus.NOT_FOUND);

    public static final BusinessCode UPDATE_DEPARTMENT_FAIL = new BusinessCode(
            ErrorCodeEnum.UPDATE_DEPARTMENT_FAIL.getValue(), "DepartmentName already exist", HttpStatus.ALREADY_REPORTED);

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
