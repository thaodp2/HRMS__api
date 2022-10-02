package com.minswap.hrms.exception.model;

import java.util.concurrent.CompletionException;

import com.minswap.hrms.model.BusinessCode;

import lombok.Getter;

@Getter
public class BaseException extends CompletionException {
    private static final long serialVersionUID = 5787607761510495730L;
    private final BusinessCode businessCode;

    public BaseException(BusinessCode businessCode) {
        super(businessCode.getMessage());
        this.businessCode = businessCode;
    }

    public BaseException(BusinessCode businessCode, String message) {
        super(message);
        this.businessCode = businessCode;
    }

    public BaseException(BusinessCode businessCode, String message, Throwable cause) {
        super(message, cause);
        this.businessCode = businessCode;
    }
}
