package com.minswap.hrms.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Getter
@Setter
public class BusinessCode implements Serializable {
    private static final long serialVersionUID = -2005206237438722822L;
    private final int code;
    private final String message;
    private HttpStatus httpStatus;

    public BusinessCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BusinessCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.httpStatus = status;
    }
}
