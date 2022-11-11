package com.minswap.hrms.constants;

import org.springframework.http.HttpStatus;

import com.minswap.hrms.model.BusinessCode;

public class ErrorCode {
    public static final BusinessCode INTERNAL_SERVER_ERROR = new BusinessCode(
    		ErrorCodeEnum.INTERNAL_SERVER_ERROR.getValue(), "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

    public static final BusinessCode DATE_INVALID = new BusinessCode(
            ErrorCodeEnum.DATE_INVALID.getValue(), "CreateDate must be before StartTime and StartTime must be before EndTime", HttpStatus.BAD_REQUEST);

    public static final BusinessCode INVALID_DATE = new BusinessCode(
            ErrorCodeEnum.INVALID_DATE.getValue(), "StartTime must be before EndTime", HttpStatus.BAD_REQUEST);

    public static final BusinessCode RESULT_NOT_FOUND = new BusinessCode(
            ErrorCodeEnum.RESULT_NOT_FOUND.getValue(), "Result not found", HttpStatus.NOT_FOUND);

    public static final BusinessCode DATE_INVALID_IN_LEAVE_REQUEST = new BusinessCode(
            ErrorCodeEnum.DATE_INVALID_IN_LEAVE_REQUEST.getValue(),
            "Start Time and End Time can't be null in leave request", HttpStatus.BAD_REQUEST);

    public static final BusinessCode INVALID_DEVICE_TYPE_ID = new BusinessCode(
            ErrorCodeEnum.INVALID_DEVICE_TYPE_ID.getValue(), "DeviceTypeId is not exist", HttpStatus.NOT_FOUND);

    public static final BusinessCode UPDATE_FAIL = new BusinessCode(
            ErrorCodeEnum.UPDATE_FAIL.getValue(), "Update failed", HttpStatus.NOT_FOUND);

    public static final BusinessCode UPDATE_DAY_OFF_FAIL = new BusinessCode(
            ErrorCodeEnum.UPDATE_DAY_OFF_FAIL.getValue(), "Your remaining days off are not enough", HttpStatus.NOT_ACCEPTABLE);

    public static final BusinessCode REQUEST_INVALID = new BusinessCode(
            ErrorCodeEnum.REQUEST_INVALID.getValue(), "You can't cancel an approved or rejected request",
            HttpStatus.NOT_ACCEPTABLE);

    public static final BusinessCode REQUEST_TYPE_INVALID = new BusinessCode(
            ErrorCodeEnum.REQUEST_TYPE_INVALID.getValue(), "Request type not found",
            HttpStatus.NOT_FOUND);

    public static final BusinessCode STATUS_INVALID = new BusinessCode(
            ErrorCodeEnum.STATUS_INVALID.getValue(), "You can't update status same as current",
            HttpStatus.NOT_ACCEPTABLE);

    public static final BusinessCode UPDATE_STATUS_FAIL = new BusinessCode(
            ErrorCodeEnum.UPDATE_STATUS_FAIL.getValue(), "You can't update an approved or rejected request",
            HttpStatus.NOT_ACCEPTABLE);

    public static final BusinessCode DELETE_FAIL = new BusinessCode(
            ErrorCodeEnum.DELETE_FAIL.getValue(), "Delete failed", HttpStatus.NOT_FOUND);

    public static final BusinessCode YEAR_INVALID = new BusinessCode(
            ErrorCodeEnum.YEAR_INVALID.getValue(), "Year can't be null", HttpStatus.NOT_FOUND);

    public static final BusinessCode MONTH_INVALID = new BusinessCode(
            ErrorCodeEnum.MONTH_INVALID.getValue(), "Month can't be null", HttpStatus.NOT_FOUND);

    public static final BusinessCode UPDATE_DEPARTMENT_FAIL = new BusinessCode(
            ErrorCodeEnum.UPDATE_DEPARTMENT_FAIL.getValue(), "DepartmentName already exist", HttpStatus.ALREADY_REPORTED);

    public static final BusinessCode INVALID_PARAMETERS = new BusinessCode(
    		ErrorCodeEnum.BAD_REQUEST.getValue(), "Invalid parameters", HttpStatus.BAD_REQUEST);
    public static final BusinessCode UNAUTHORIZED = new BusinessCode(
    		ErrorCodeEnum.UNAUTHORIZED.getValue(), "Unauthorized", HttpStatus.UNAUTHORIZED);
    public static final BusinessCode DUPLICATE_DEVICE_TYPE = new BusinessCode(
            ErrorCodeEnum.DUPLICATE_DEVICE_TYPE.getValue(), "Duplicate device type", HttpStatus.BAD_REQUEST);

    public static final BusinessCode NOT_FOUND_DEVICE_TYPE = new BusinessCode(
            ErrorCodeEnum.NOT_FOUND_DEVICE_TYPE.getValue(), "Not found device type", HttpStatus.NOT_FOUND);

    public static final BusinessCode PERSON_NOT_EXIST = new BusinessCode(
            ErrorCodeEnum.PERSON_NOT_EXIST.getValue(), "Person not exist!", HttpStatus.NOT_FOUND);
    public static BusinessCode newErrorCode(String message) {
        return new BusinessCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR.getValue(), message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public static final BusinessCode DATE_FAIL_FOMART = new BusinessCode(
            ErrorCodeEnum.DATE_INVALID.getValue(), "Date fail formart! ", HttpStatus.BAD_REQUEST);
    public static BusinessCode newErrorCode(int code,String message) {
      return new BusinessCode(code, message, null);
    }

    public static BusinessCode newErrorCode(int code,String message, HttpStatus httpStatus) {
      return new BusinessCode(code, message, httpStatus);
    }
    private ErrorCode() {
    }
}
