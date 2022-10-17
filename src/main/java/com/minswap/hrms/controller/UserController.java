package com.minswap.hrms.controller;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.UpdateUserRequest;
import com.minswap.hrms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PutMapping("/user/information")
    public ResponseEntity<BaseResponse<HttpStatus, Void>> getRequestDetail(@RequestBody UpdateUserRequest updateUserDto) throws Exception {
        return userService.updateUserInformation(updateUserDto);
    }
}
