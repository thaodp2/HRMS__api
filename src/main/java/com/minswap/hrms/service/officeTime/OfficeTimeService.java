package com.minswap.hrms.service.officeTime;

import com.minswap.hrms.entities.OfficeTime;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.OfficeTimeRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface OfficeTimeService {

    ResponseEntity<BaseResponse<HttpStatus, Void>> updateOfficeTime(OfficeTimeRequest officeTimeRequest) throws Exception;

    ResponseEntity<BaseResponse<OfficeTime, Void>> getOfficeTime() throws Exception;
}
