package com.minswap.hrms.service.request;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.response.dto.ListRequestDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface RequestService {
//    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getAllRequest(int page, int limit);
//    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getSubordinateRequest(Integer managerId, int page, int limit);
//    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getMyRequest(Integer personId, int page, int limit);

    ResponseEntity<BaseResponse<RequestResponse, Void>> getEmployeeRequestDetail (Long id);

    ResponseEntity<BaseResponse<Void, Void>> updateRequestStatus (String status, Long id);

    ResponseEntity<BaseResponse<ListRequestDto, Pageable>> searchRequest (Long userId, String startDate, String endDate,
                                                                          Integer page, Integer limit) throws Exception;
}
