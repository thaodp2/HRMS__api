package com.minswap.hrms.controller.user;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.UpdateUserDto;
import com.minswap.hrms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(CommonConstant.USER)
public class UserController {

    @Autowired
    UserService userService;

    @PutMapping("/information")
    public ResponseEntity<BaseResponse<HttpStatus, Void>> getRequestDetail(@RequestBody UpdateUserDto updateUserDto) throws Exception {
        return userService.updateUserInformation(updateUserDto);
    }
}
