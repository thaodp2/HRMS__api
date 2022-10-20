package com.minswap.hrms.service.request;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.response.dto.ListRequestDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Date;

public interface RequestService {
//    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getAllRequest(int page, int limit);
//    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getSubordinateRequest(Integer managerId, int page, int limit);
//    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getMyRequest(Integer personId, int page, int limit);
    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getAllLeaveBenefitRequest(Integer page, Integer limit, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId);

    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getAllDeviceRequest(Integer page, Integer limit, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId);
    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getSubordinateLeaveBenefitRequest(Long managerId, Integer page, Integer limit, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId);
    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getSubordinateDeviceRequest(Long managerId, Integer page, Integer limit, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId);
    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getMyLeaveBenefitRequest(Long personId, Integer page, Integer limit, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId);

    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getMyDeviceRequest(Long personId, Integer page, Integer limit, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId);

    ResponseEntity<BaseResponse<RequestResponse, Void>> getEmployeeRequestDetail (Long id);

    ResponseEntity<BaseResponse<Void, Void>> updateRequestStatus (String status, Long id);

    ResponseEntity<BaseResponse<ListRequestDto, Pageable>> searchRequest (Long userId, String startDate, String endDate,
                                                                          Integer page, Integer limit) throws Exception;
}
