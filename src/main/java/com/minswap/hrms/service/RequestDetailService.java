package com.minswap.hrms.service;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.RequestDetailRespone;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


public interface RequestDetailService {

    ResponseEntity<BaseResponse<RequestDetailRespone, Void>> getEmployeeRequestDetail (Long id);

}
