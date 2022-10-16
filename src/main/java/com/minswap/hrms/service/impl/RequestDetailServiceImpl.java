package com.minswap.hrms.service.impl;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.model.BusinessCode;
import com.minswap.hrms.repsotories.RequestDetailRepository;
import com.minswap.hrms.repsotories.RequestStatusRepository;
import com.minswap.hrms.response.RequestDetailRespone;
import com.minswap.hrms.response.dto.RequestDetailDto;
import com.minswap.hrms.service.RequestDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RequestDetailServiceImpl implements RequestDetailService {


    @Autowired
    RequestDetailRepository requestDetailRepository;

    @Autowired
    RequestStatusRepository requestStatusRepository;

    private static final Integer UPDATE_SUCCESS = 1;

    private static final Integer UPDATE_FAIL = 0;

    @Override
    public ResponseEntity<BaseResponse<RequestDetailRespone, Void>> getEmployeeRequestDetail(Long id) {
        try {
                RequestDetailDto requestDetailDto
                        = requestDetailRepository.getEmployeeRequestDetail(id);
                RequestDetailRespone requestDetailRespone = new RequestDetailRespone(requestDetailDto);
                ResponseEntity<BaseResponse<RequestDetailRespone, Void>> responseEntity
                        = BaseResponse.ofSucceededOffset(requestDetailRespone, null);
                return responseEntity;
        }
        catch (Exception e) {
            try {
                throw new Exception(e.getMessage());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> updateRequestStatus(String status, Long id) {
        Integer isUpdatedSuccess = requestStatusRepository.updateRequest(status, id);
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = null;
        if (isUpdatedSuccess == UPDATE_SUCCESS) {
            responseEntity = BaseResponse.ofSucceeded(null);
        }
        else {
            responseEntity = BaseResponse.ofFailedUpdate(null);
        }
        return responseEntity;
    }
}
