package com.minswap.hrms.service.person;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.UpdateUserRequest;
import com.minswap.hrms.response.MasterDataResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface PersonService {
    ResponseEntity<BaseResponse<HttpStatus, Void>> updateUserInformation(UpdateUserRequest persondto) throws Exception;

    ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataAllManager (String search);
}
