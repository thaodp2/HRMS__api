package com.minswap.hrms.service.request;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.CreateRequest;
import com.minswap.hrms.request.EditRequest;
import com.minswap.hrms.response.RequestResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;


public interface RequestService {
    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getAllRequest(Integer page, Integer limit,String search, String createDateFrom, String createDateTo, Long requestTypeId, String status, String sort, String dir) throws ParseException;
    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getSubordinateRequest(Long managerId, Integer page, Integer limit,String search, String createDateFrom, String createDateTo, Long requestTypeId, String status, String sort, String dir) throws ParseException;
    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getMyRequest(Long personId, Integer page, Integer limit,String search, String createDateFrom, String createDateTo, Long requestTypeId, String status, String sort, String dir) throws ParseException;

    ResponseEntity<BaseResponse<RequestResponse, Void>> getEmployeeRequestDetail (Long id);

    ResponseEntity<BaseResponse<Void, Void>> updateRequestStatus (String status, Long id) throws ParseException;

    ResponseEntity<BaseResponse<Void, Void>> editRequest (EditRequest editRequest,
                                                          Long id) throws ParseException;
    ResponseEntity<BaseResponse<Void, Void>> createRequest(CreateRequest createRequest) throws ParseException;

    void autoUpdateRequestStatus();

    ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getBorrowDeviceRequestList(Integer page, Integer limit, String search, String approvalDateFrom, String approvalDateTo, Long deviceTypeId,Integer isAssigned, String sort, String dir) throws ParseException;
}
