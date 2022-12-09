package com.minswap.hrms.controller.hr;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.SignatureProfileResponse;
import com.minswap.hrms.service.signatureProfile.SignatureProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CommonConstant.HR + "/")
@Validated
public class HRSignatureProfileController {
    @Autowired
    SignatureProfileService signatureProfileService;

    @GetMapping("/signature_register")
    public ResponseEntity<BaseResponse<SignatureProfileResponse, Pageable>> registerSignatureEmployee(
            @RequestParam(required = false) int isRegistered,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String dir,
            @RequestParam int page,
            @RequestParam int limit) {

        return signatureProfileService.listSignatureRegister(isRegistered, sort, dir, page, limit);
    }
}
