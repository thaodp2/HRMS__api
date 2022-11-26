package com.minswap.hrms.service.signatureProfile;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.SignatureProfileRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface SignatureProfileService {

    public ResponseEntity<BaseResponse<Void, Void>> registerSignature(SignatureProfileRequest signatureProfileRequest);
}
