package com.minswap.hrms.service.request;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.EditDeviceRequest;
import com.minswap.hrms.request.EditLeaveBenefitRequest;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.response.dto.ListRequestDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;


public interface RequestService {
    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getAllRequest(Integer page, Integer limit, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId) throws ParseException;
    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getSubordinateRequest(Long managerId, Integer page, Integer limit, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId) throws ParseException;
    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getMyRequest(Long personId, Integer page, Integer limit, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId) throws ParseException;

    ResponseEntity<BaseResponse<RequestResponse, Void>> getEmployeeRequestDetail (Long id);

    ResponseEntity<BaseResponse<Void, Void>> updateRequestStatus (String status, Long id);

    ResponseEntity<BaseResponse<Void, Void>> editLeaveBenefitRequest (EditLeaveBenefitRequest editLeaveBenefitRequest,
                                                                      Long id);

    ResponseEntity<BaseResponse<Void, Void>> editDeviceRequest (EditDeviceRequest editDeviceRequest,
                                                                Long id);
//
//    ResponseEntity<BaseResponse<ListRequestDto, Pageable>> searchRequest (Long userId, String startDate, String endDate,
//                                                                          Integer page, Integer limit) throws Exception;
}
