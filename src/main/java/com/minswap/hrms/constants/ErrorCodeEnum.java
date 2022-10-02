package com.minswap.hrms.constants;

/** The Enum ErrorCodeEnum. */
public enum ErrorCodeEnum {

  /** The success. */
  SUCCESS(200000),
  BAD_REQUEST(400000),
  API_KEY_ERROR(400012),
  INTERNAL_SERVER_ERROR(500001),
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
