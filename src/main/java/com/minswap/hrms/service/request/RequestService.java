package com.minswap.hrms.service.request;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.EmployeeListResponse;
import com.minswap.hrms.response.RequestResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface RequestService {
    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getAllRequest(int page, int limit);
    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getSubordinateRequest(Integer managerId, int page, int limit);

    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getMyRequest(Integer pesonId, int page, int limit);

}
