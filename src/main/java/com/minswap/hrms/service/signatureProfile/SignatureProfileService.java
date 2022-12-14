package com.minswap.hrms.service.signatureProfile;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.SignatureProfileRequest;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.SignatureProfileResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface SignatureProfileService {

    ResponseEntity<BaseResponse<Void, Void>> updateSignatureRegister(SignatureProfileRequest signatureProfileRequest);
    ResponseEntity<BaseResponse<Void, Void>> deleteSignatureRegister(SignatureProfileRequest signatureProfileRequest);
    ResponseEntity<BaseResponse<SignatureProfileResponse, Pageable>> listSignatureRegister(Integer isRegistered, String search, int page, int limit);

}
