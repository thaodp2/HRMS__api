package com.minswap.hrms.exception.handler;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.model.BusinessCode;
import com.minswap.hrms.model.Meta;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	@ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<Void, Void>> handleException(BaseException exception) {
        log.error(ExceptionUtils.getStackTrace(exception));

        BusinessCode errorCode = exception.getBusinessCode();
        Meta<Void> meta = Meta.buildMeta(errorCode, null);
        return BaseResponse.ofFailed(meta, errorCode.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void, Void>> handleException(Exception exception) {
        log.error(ExceptionUtils.getStackTrace(exception));

        BusinessCode errorCode = new BusinessCode(null, exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        Meta<Void> meta = Meta.buildMeta(errorCode, null);
        return BaseResponse.ofFailed(meta, errorCode.getHttpStatus());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class,
            HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<BaseResponse<Void, Void>> handleException(RuntimeException exception) {
        log.error(ExceptionUtils.getStackTrace(exception));

        Meta<Void> meta = Meta.buildMeta(ErrorCode.INVALID_PARAMETERS, null);
        return BaseResponse.ofFailed(meta, ErrorCode.INVALID_PARAMETERS.getHttpStatus());
    }
}
