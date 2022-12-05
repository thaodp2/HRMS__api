package com.minswap.hrms.controller;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.UpdateSecureCodeRequest;
import com.minswap.hrms.security.oauth2.CurrentUser;
import com.minswap.hrms.service.person.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.minswap.hrms.security.UserPrincipal;


@RestController
public class PinCodeController {

    @Autowired
    PersonService personService;

    @PostMapping("/secure-code/correct")
    public ResponseEntity<BaseResponse<Boolean, Void>> checkPinCodeIsCorrect(@CurrentUser UserPrincipal userPrincipal,
                                                                             @RequestBody UpdateSecureCodeRequest secureCodeRequest) {
        Long personId = personService.getPersonInforByEmail(userPrincipal.getEmail()).getPersonId();
        return personService.checkSecureCodeIsCorrect(secureCodeRequest, personId);
    }

    @GetMapping("/secure-code")
    public ResponseEntity<BaseResponse<Boolean, Void>> checkPinCodeIsExist(@CurrentUser UserPrincipal userPrincipal) {
        Long personId = personService.getPersonInforByEmail(userPrincipal.getEmail()).getPersonId();
        return personService.checkSecureCodeIsExist(personId);
    }

    @GetMapping("/secure-code/forgot")
    public ResponseEntity<BaseResponse<Boolean, Void>> forgotPinCode(@CurrentUser UserPrincipal userPrincipal) {
        Long personId = personService.getPersonInforByEmail(userPrincipal.getEmail()).getPersonId();
        return personService.forgotPinCode(personId);
    }

    @PutMapping("/secure-code")
    public ResponseEntity<BaseResponse<Boolean, Void>> updatePinCode(@CurrentUser UserPrincipal userPrincipal,
                                                                     @RequestBody UpdateSecureCodeRequest secureCodeRequest) {
        Long personId = personService.getPersonInforByEmail(userPrincipal.getEmail()).getPersonId();
        return personService.updatePinCode(secureCodeRequest, personId);
    }

    @PostMapping("/secure-code")
    public ResponseEntity<BaseResponse<Boolean, Void>> createPinCode(@CurrentUser UserPrincipal userPrincipal,
                                                                     @RequestBody UpdateSecureCodeRequest secureCodeRequest) {
        Long personId = personService.getPersonInforByEmail(userPrincipal.getEmail()).getPersonId();

        return personService.createPinCode(secureCodeRequest, personId);
    }
}
