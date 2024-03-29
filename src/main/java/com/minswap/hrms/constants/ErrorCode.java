package com.minswap.hrms.constants;

import org.springframework.http.HttpStatus;

import com.minswap.hrms.model.BusinessCode;

public class ErrorCode {
    public static final BusinessCode INTERNAL_SERVER_ERROR = new BusinessCode(
    		ErrorCodeEnum.INTERNAL_SERVER_ERROR.getValue(), "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

    public static final BusinessCode DATE_INVALID = new BusinessCode(
            ErrorCodeEnum.DATE_INVALID.getValue(), "Create date must be before start time " +
            "and start time must be before end time", HttpStatus.BAD_REQUEST);

    public static final BusinessCode INVALID_DATE = new BusinessCode(
            ErrorCodeEnum.INVALID_DATE.getValue(), "StartTime must be before EndTime", HttpStatus.BAD_REQUEST);

    public static final BusinessCode PAYSLIP_NOT_EXIST = new BusinessCode(
            ErrorCodeEnum.PAYSLIP_NOT_EXIST.getValue(), "You have no payslip in this month", HttpStatus.BAD_REQUEST);

    public static final BusinessCode HAVE_NO_PERMISSION = new BusinessCode(
            ErrorCodeEnum.HAVE_NO_PERMISSION.getValue(), "The security code entered is wrong", HttpStatus.BAD_REQUEST);
    public static final BusinessCode RESULT_NOT_FOUND = new BusinessCode(
            ErrorCodeEnum.RESULT_NOT_FOUND.getValue(), "Result not found", HttpStatus.NOT_FOUND);

    public static final BusinessCode DATE_INVALID_IN_LEAVE_REQUEST = new BusinessCode(
            ErrorCodeEnum.DATE_INVALID_IN_LEAVE_REQUEST.getValue(),
            "Start Time and End Time can't be null in leave request", HttpStatus.BAD_REQUEST);

    public static final BusinessCode INVALID_DEVICE_TYPE_ID = new BusinessCode(
            ErrorCodeEnum.INVALID_DEVICE_TYPE_ID.getValue(), "Device type is not exist", HttpStatus.NOT_FOUND);

    public static final BusinessCode UPDATE_FAIL = new BusinessCode(
            ErrorCodeEnum.UPDATE_FAIL.getValue(), "Update failed", HttpStatus.NOT_FOUND);

    public static final BusinessCode INVALID_DATA = new BusinessCode(
            ErrorCodeEnum.INVALID_DATA.getValue(), "Data invalid", HttpStatus.NOT_ACCEPTABLE);

    public static final BusinessCode INVALID_FILE = new BusinessCode(
            ErrorCodeEnum.INVALID_FILE.getValue(), "File invalid", HttpStatus.NOT_ACCEPTABLE);

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

    public static final BusinessCode UPLOAD_EXCEL = new BusinessCode(
            ErrorCodeEnum.UPLOAD_EXCEL.getValue(), "Please upload excel file!", HttpStatus.NOT_FOUND);

    public static final BusinessCode MONTH_INVALID = new BusinessCode(
            ErrorCodeEnum.MONTH_INVALID.getValue(), "Month can't be null", HttpStatus.NOT_FOUND);

    public static final BusinessCode INVALID_DEPARTMENT = new BusinessCode(
            ErrorCodeEnum.INVALID_DEPARTMENT.getValue(), "Department already exist", HttpStatus.ALREADY_REPORTED);

    public static final BusinessCode INVALID_POSITION = new BusinessCode(
            ErrorCodeEnum.INVALID_POSITION.getValue(), "Position already exist", HttpStatus.ALREADY_REPORTED);

    public static final BusinessCode INVALID_PARAMETERS = new BusinessCode(
    		ErrorCodeEnum.BAD_REQUEST.getValue(), "Invalid parameters", HttpStatus.BAD_REQUEST);
    public static final BusinessCode UNAUTHORIZED = new BusinessCode(
    		ErrorCodeEnum.UNAUTHORIZED.getValue(), "Unauthorized", HttpStatus.UNAUTHORIZED);
    public static final BusinessCode DUPLICATE_DEVICE_TYPE = new BusinessCode(
            ErrorCodeEnum.DUPLICATE_DEVICE_TYPE.getValue(), "Duplicate device type", HttpStatus.BAD_REQUEST);

    public static final BusinessCode DUPLICATE_DEVICE_CODE = new BusinessCode(
            ErrorCodeEnum.DUPLICATE_DEVICE_CODE.getValue(), "Duplicate device code", HttpStatus.BAD_REQUEST);

    public static final BusinessCode DEVICE_NOT_EXIST = new BusinessCode(
            ErrorCodeEnum.DEVICE_NOT_EXIST.getValue(), "Device not exist", HttpStatus.BAD_REQUEST);

    public static final BusinessCode DEVICE_HAS_BEEN_BORROWED = new BusinessCode(
            ErrorCodeEnum.DEVICE_HAS_BEEN_BORROWED.getValue(), "Device has been borrowed, can not update device type", HttpStatus.BAD_REQUEST);

    public static final BusinessCode DEVICE_TYPE_NULL_OR_EMPTY = new BusinessCode(
            ErrorCodeEnum.DEVICE_TYPE_NULL_OR_EMPTY.getValue(), "Device type can't null or empty", HttpStatus.BAD_REQUEST);

    public static final BusinessCode NOT_FOUND_DEVICE_TYPE = new BusinessCode(
            ErrorCodeEnum.NOT_FOUND_DEVICE_TYPE.getValue(), "Not found device type", HttpStatus.NOT_FOUND);

    public static final BusinessCode NO_DATA = new BusinessCode(
            ErrorCodeEnum.NO_DATA.getValue(), "Don't have data", HttpStatus.NO_CONTENT);

    public static final BusinessCode LIST_POSITION_NAME_EMPTY = new BusinessCode(
            ErrorCodeEnum.LIST_POSITION_NAME_EMPTY.getValue(), "List position name can't be empty!", HttpStatus.NO_CONTENT);

    public static final BusinessCode PERSON_NOT_EXIST = new BusinessCode(
            ErrorCodeEnum.PERSON_NOT_EXIST.getValue(), "Person not exist!", HttpStatus.NOT_FOUND);

    public static final BusinessCode CURRENT_SECURE_CODE_INCORRECT = new BusinessCode(
            ErrorCodeEnum.CURRENT_SECURE_CODE_INCORRECT.getValue(), "Current Secure Code Incorrect", HttpStatus.BAD_REQUEST);

    public static final BusinessCode SECURE_CODE_AND_CONFIRM_CODE_DO_NOT_MATCH = new BusinessCode(
            ErrorCodeEnum.SECURE_CODE_AND_CONFIRM_CODE_DO_NOT_MATCH.getValue(), "Secure code and confirm code do not match", HttpStatus.BAD_REQUEST);

    public static final BusinessCode NEW_CODE_AND_CURRENT_CODE_MUST_DIFFERENT = new BusinessCode(
            ErrorCodeEnum.SECURE_CODE_AND_CONFIRM_CODE_DO_NOT_MATCH.getValue(), "New secure code and current code should be different", HttpStatus.BAD_REQUEST);

    public static final BusinessCode CREATE_FAIL = new BusinessCode(
            ErrorCodeEnum.CREATE_FAIL.getValue(), "Create fail!", HttpStatus.EXPECTATION_FAILED);

    public static final BusinessCode DO_NOT_ENOUGH_DEVICE_TO_ASSIGN = new BusinessCode(
            ErrorCodeEnum.DO_NOT_ENOUGH_DEVICE_TO_ASSIGN.getValue(), "Don't have any device to assign!", HttpStatus.EXPECTATION_FAILED);

    public static BusinessCode newErrorCode(String message) {
        return new BusinessCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR.getValue(), message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public static final BusinessCode DATE_FAIL_FOMART = new BusinessCode(
            ErrorCodeEnum.DATE_INVALID.getValue(), "Date fail formart! ", HttpStatus.BAD_REQUEST);
    public static final BusinessCode SIGNATURE_NOT_EXIST = new BusinessCode(
            ErrorCodeEnum.SIGNATURE_NOT_EXIST.getValue(), "Signature not exist!", HttpStatus.NOT_FOUND);
    public static BusinessCode newErrorCode(int code,String message) {
      return new BusinessCode(code, message, null);
    }

    public static BusinessCode newErrorCode(int code,String message, HttpStatus httpStatus) {
      return new BusinessCode(code, message, httpStatus);
    }
    private ErrorCode() {
    }
    public static final BusinessCode CITIZEN_INDENTIFICATION_EXSIT = new BusinessCode(
            ErrorCodeEnum.CITIZEN_INDENTIFICATION_EXSIT.getValue(), "Citizen Identification exist!", HttpStatus.NOT_FOUND);

    public static final BusinessCode SEND_PIN_CODE_FAILED = new BusinessCode(
            ErrorCodeEnum.SEND_PIN_CODE_FAILED.getValue(), "Send pin code to your email failed!", HttpStatus.NOT_FOUND);

    public static final BusinessCode FINGERPRINT_INVALID = new BusinessCode(
            ErrorCodeEnum.FINGERPRINT_INVALID.getValue(), "Contact HR to register this fingerprint!", HttpStatus.NOT_FOUND);
    public static final BusinessCode UNAUTHORIZE = new BusinessCode(
            ErrorCodeEnum.UNAUTHORIZE.getValue(), "Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication", HttpStatus.UNAUTHORIZED);

    public static final BusinessCode SEND_FAIL = new BusinessCode(
            ErrorCodeEnum.SEND_FAIL.getValue(), "Send email fail", HttpStatus.EXPECTATION_FAILED);
}
