package com.minswap.hrms.service;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.RequestDetailRespone;
import com.minswap.hrms.response.dto.ListRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


public interface RequestDetailService {

    ResponseEntity<BaseResponse<RequestDetailRespone, Void>> getEmployeeRequestDetail (Long id);

    ResponseEntity<BaseResponse<Void, Void>> updateRequestStatus (String status, Long id);

    ResponseEntity<BaseResponse<ListRequestDto, Void>> searchRequest (Long userId, String startDate, String endDate, Integer page,
                                                                      Integer limit);

}
