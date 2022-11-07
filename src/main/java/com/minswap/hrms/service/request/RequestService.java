package com.minswap.hrms.service.request;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.EditRequest;
import com.minswap.hrms.response.RequestResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;


public interface RequestService {
    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getAllRequest(Integer page, Integer limit, String createDateFrom, String createDateTo, Long requestTypeId, String status, String sort, String dir);
    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getSubordinateRequest(Long managerId, Integer page, Integer limit, String createDateFrom, String createDateTo, Long requestTypeId, String status, String sort, String dir);
    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getMyRequest(Long personId, Integer page, Integer limit, String createDateFrom, String createDateTo, Long requestTypeId, String status, String sort, String dir);

    ResponseEntity<BaseResponse<RequestResponse, Void>> getEmployeeRequestDetail (Long id);

    ResponseEntity<BaseResponse<Void, Void>> updateRequestStatus (String status, Long id) throws ParseException;

    ResponseEntity<BaseResponse<Void, Void>> editRequest (EditRequest editRequest,
                                                          Long id);
//
//    ResponseEntity<BaseResponse<ListRequestDto, Pageable>> searchRequest (Long userId, String startDate, String endDate,
//                                                                          Integer page, Integer limit) throws Exception;

//    ResponseEntity<BaseResponse<ListRequestDto, Pageable>> searchRequest (Long userId, String startDate, String endDate,
//                                                                          Integer page, Integer limit) throws Exception;
    ResponseEntity<BaseResponse<Void, Void>> cancelRequest (Long id);

}
