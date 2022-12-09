package com.minswap.hrms.service.signatureProfile;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.SignatureProfileRequest;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.SignatureProfileResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface SignatureProfileService {

    public ResponseEntity<BaseResponse<Void, Void>> updateSignatureRegister(SignatureProfileRequest signatureProfileRequest);
    public ResponseEntity<BaseResponse<SignatureProfileResponse, Pageable>> listSignatureRegister(int isRegistered, String sort, String dir, int page, int limit);

    ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataAllEmployee (String search);

}
