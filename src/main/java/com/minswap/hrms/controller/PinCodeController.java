package com.minswap.hrms.controller;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.service.person.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PinCodeController {

    @Autowired
    PersonService personService;

    @GetMapping("/pin-code")
    public ResponseEntity<BaseResponse<Boolean, Void>> checkPinCode(@RequestParam String pinCode) {
        return personService.checkPinCode(pinCode);
    }
}
