package com.minswap.hrms.service;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.UpdateUserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<BaseResponse<HttpStatus, Void>> updateUserInformation(UpdateUserDto persondto) throws Exception;
}
