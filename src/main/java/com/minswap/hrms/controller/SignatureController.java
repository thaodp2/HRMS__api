package com.minswap.hrms.controller;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.SignatureMasterDataResponse;
import com.minswap.hrms.service.signatureProfile.SignatureProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignatureController {

    @Autowired
    SignatureProfileService signatureProfileService;

    @GetMapping("/signature-master-data")
    public ResponseEntity<BaseResponse<SignatureMasterDataResponse, Pageable>> getMasterDataSignature(@RequestParam(required = false) String search) {
        return signatureProfileService.getMasterDataSignature(search);
    }


}
