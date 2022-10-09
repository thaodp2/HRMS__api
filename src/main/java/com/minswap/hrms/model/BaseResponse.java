package com.minswap.hrms.model;

import com.minswap.hrms.exception.model.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@Accessors(chain = true)
@Slf4j
public class BaseResponse<T, R> {
    private Meta<R> meta = new Meta<>();
    private T data;

    private static final BusinessCode SUCCESS_BUSINESS_CODE = new BusinessCode("SUCCESS", "Success", HttpStatus.OK);

    public static <T, R> ResponseEntity<BaseResponse<T, R>> ofSucceeded(T data) {
        BaseResponse<T, R> response = new BaseResponse<>();
        response.data = data;
        response.meta = Meta.buildMeta(SUCCESS_BUSINESS_CODE, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public static <T, R> ResponseEntity<BaseResponse<T, R>> ofSucceededOffset(T data, R extraMeta) {
      BaseResponse<T, R> response = new BaseResponse<>();
      response.data = data;
      response.meta = Meta.buildMeta(SUCCESS_BUSINESS_CODE, extraMeta);
      return new ResponseEntity<>(response, HttpStatus.OK);
  }
    public static <R> ResponseEntity<BaseResponse<Void, R>> ofFailed(Meta<R> meta, HttpStatus httpStatus) {
        BaseResponse<Void, R> response = new BaseResponse<>();
        response.meta = meta;
        return new ResponseEntity<>(response, httpStatus);
        }
    
}
