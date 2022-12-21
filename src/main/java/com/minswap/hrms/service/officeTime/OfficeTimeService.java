package com.minswap.hrms.service.officeTime;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.OfficeTimeRequest;
import com.minswap.hrms.response.OfficeTimeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface OfficeTimeService {

    ResponseEntity<BaseResponse<HttpStatus, Void>> updateOfficeTime(OfficeTimeRequest officeTimeRequest) throws Exception;

    ResponseEntity<BaseResponse<OfficeTimeResponse, Void>> getOfficeTime() throws Exception;

    ResponseEntity<BaseResponse<HttpStatus, Void>> updateLunchBreak(OfficeTimeRequest officeTimeRequest);
}
