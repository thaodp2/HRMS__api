package com.minswap.hrms.controller.hr;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.SignatureProfileRequest;
import com.minswap.hrms.response.SignatureProfileResponse;
import com.minswap.hrms.service.signatureProfile.SignatureProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(CommonConstant.HR + "/")
@Validated
public class HRSignatureProfileController {
    @Autowired
    SignatureProfileService signatureProfileService;

    @GetMapping("/signature_register")
    public ResponseEntity<BaseResponse<SignatureProfileResponse, Pageable>> getListSignatureEmployee(
            @RequestParam(required = false) Integer isRegistered,
            @RequestParam(required = false) String search,
            @RequestParam int page,
            @RequestParam int limit) {

        return signatureProfileService.listSignatureRegister(isRegistered, search, page, limit);
    }

    @PostMapping("/signature_register")
    public ResponseEntity<BaseResponse<Void, Void>> registerSignature(@RequestBody SignatureProfileRequest request) {
        return signatureProfileService.updateSignatureRegister(request);
    }

    @DeleteMapping("/signature_register")
    public ResponseEntity<BaseResponse<Void, Void>> deleteSignatureRegister(@RequestBody SignatureProfileRequest request) {
        return signatureProfileService.deleteSignatureRegister(request);
    }
}
