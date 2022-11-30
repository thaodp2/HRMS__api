package com.minswap.hrms.controller;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.UpdateSecureCodeRequest;
import com.minswap.hrms.service.person.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
public class PinCodeController {

    @Autowired
    PersonService personService;

    @PostMapping("/secure-code/correct")
    public ResponseEntity<BaseResponse<Boolean, Void>> checkPinCodeIsCorrect(@RequestBody UpdateSecureCodeRequest secureCodeRequest) {
        return personService.checkSecureCodeIsCorrect(secureCodeRequest);
    }

    @GetMapping("/secure-code")
    public ResponseEntity<BaseResponse<Boolean, Void>> checkPinCodeIsExist() {
        return personService.checkSecureCodeIsExist();
    }

    @GetMapping("/secure-code/forgot")
    public ResponseEntity<BaseResponse<Boolean, Void>> forgotPinCode() {
        return personService.forgotPinCode();
    }

    @PutMapping("/secure-code")
    public ResponseEntity<BaseResponse<Boolean, Void>> updatePinCode(@RequestBody UpdateSecureCodeRequest secureCodeRequest) {
        return personService.updatePinCode(secureCodeRequest);
    }

    @PostMapping("/secure-code")
    public ResponseEntity<BaseResponse<Boolean, Void>> createPinCode(@RequestBody UpdateSecureCodeRequest secureCodeRequest) {
        return personService.createPinCode(secureCodeRequest);
    }
}
