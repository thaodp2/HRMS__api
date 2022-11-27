package com.minswap.hrms.constants;

/** The Enum ErrorCodeEnum. */
public enum ErrorCodeEnum {

  /** The success. */
  SUCCESS(200),
  BAD_REQUEST(400),
  INTERNAL_SERVER_ERROR(500),
  UNAUTHORIZED(501),

  REQUEST_INVALID(406),

  DATE_INVALID(405),

  INVALID_DATE(405),

  RESULT_NOT_FOUND(404),

  UPDATE_FAIL(404),

  UPDATE_STATUS_FAIL(404),

  DELETE_FAIL(404),

  INVALID_DEPARTMENT(208),

  DUPLICATE_DEVICE_TYPE(416),

  DUPLICATE_DEVICE_CODE(418),

  DEVICE_NOT_EXIST(420),

  NOT_FOUND_DEVICE_TYPE(416),
  DEVICE_TYPE_NULL_OR_EMPTY(417),
  NO_DATA(418),
  PERSON_NOT_EXIST(455),

  YEAR_INVALID(416),

  MONTH_INVALID(416),

  STATUS_INVALID(416),

  UPDATE_DAY_OFF_FAIL(406),

  REQUEST_TYPE_INVALID(404),

  INVALID_DEVICE_TYPE_ID(404),

  DATE_INVALID_IN_LEAVE_REQUEST(400),

  INVALID_POSITION(208),

  LIST_POSITION_NAME_EMPTY(204),

  DO_NOT_ENOUGH_DEVICE_TO_ASSIGN(678)

  ;

  /** The value. */
  private int value;

  /**
   * Instantiates a new error code enum.
   *
   * @param value the value
   */
  ErrorCodeEnum(int value) {
    this.value = value;
  }

  /** Instantiates a new error code enum. */
  ErrorCodeEnum() {}

  /**
   * Gets the value.
   *
   * @return the value
   */
  public int getValue() {
    return value;
  }

  /**
   * Sets the value.
   *
   * @param value the new value
   */
  public void setValue(int value) {
    this.value = value;
  }
}
