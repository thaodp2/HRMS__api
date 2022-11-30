package com.minswap.hrms.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import static com.minswap.hrms.constants.ErrorCode.INVALID_FILE;

@Data
@Accessors(chain = true)
@Slf4j
public class BaseResponse<T, R> {
    private Meta<R> metadata = new Meta<>();
    private T data;

    private static final BusinessCode SUCCESS_BUSINESS_CODE = new BusinessCode(200, "Success", HttpStatus.OK);

    private static final BusinessCode OTHER_REMAINING_TIME_BUDGET = new BusinessCode(200, "This request doesn't have remaining time", HttpStatus.OK);

    public static <T, R> ResponseEntity<BaseResponse<T, R>> ofSucceeded(T data) {
        BaseResponse<T, R> response = new BaseResponse<>();
        response.data = data;
        response.metadata = Meta.buildMeta(SUCCESS_BUSINESS_CODE, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public static <T, R> ResponseEntity<BaseResponse<T, R>> ofSucceededOffset(T data, R extraMeta) {
      BaseResponse<T, R> response = new BaseResponse<>();
      response.data = data;
      response.metadata = Meta.buildMeta(SUCCESS_BUSINESS_CODE, extraMeta);
      return new ResponseEntity<>(response, HttpStatus.OK);
  }

    public static <T, R> ResponseEntity<BaseResponse<T, R>> ofSucceededOffset(T data, R extraMeta, String message) {
        BaseResponse<T, R> response = new BaseResponse<>();
        response.data = data;
        response.metadata = Meta.buildMeta(SUCCESS_BUSINESS_CODE, extraMeta);
        response.metadata.setMessage(message);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static <T, R> ResponseEntity<BaseResponse<T, R>> ofFailedCustom(Meta<R> meta, R extraMeta) {
        BaseResponse<T, R> response = new BaseResponse<>();
        response.metadata = meta;
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    public static <R> ResponseEntity<BaseResponse<Void, R>> ofFailed(Meta<R> meta, HttpStatus httpStatus) {
        BaseResponse<Void, R> response = new BaseResponse<>();
        response.metadata = meta;
        return new ResponseEntity<>(response, httpStatus);
    }

    // Vunt
    public static <T, R> ResponseEntity<BaseResponse<T, R>> ofSucceededRemainingTime(T data, R extraMeta) {
        BaseResponse<T, R> response = new BaseResponse<>();
        response.data = data;
        response.metadata = Meta.buildMeta(OTHER_REMAINING_TIME_BUDGET, extraMeta);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
