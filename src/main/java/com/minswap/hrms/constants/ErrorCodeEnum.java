package com.minswap.hrms.constants;

/** The Enum ErrorCodeEnum. */
public enum ErrorCodeEnum {

  /** The success. */
  SUCCESS(200),
  BAD_REQUEST(400),
  INTERNAL_SERVER_ERROR(500),
  UNAUTHORIZED(501),

  DATE_INVALID(405),

  RESULT_NOT_FOUND(404),

  UPDATE_STATUS_FAIL(404),
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
