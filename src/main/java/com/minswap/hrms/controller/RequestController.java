package com.minswap.hrms.controller;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.response.dto.ListRequestDto;
import com.minswap.hrms.response.dto.StatusDto;
import com.minswap.hrms.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RequestController {
    @Autowired
    private RequestService requestService;

//    @GetMapping("/employee/{employeeId}/request")
//    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getListEmployee(@PathVariable Integer employeeId, @RequestParam int page, @RequestParam int limit) {
//        return requestService.getMyRequest(employeeId,page, limit);
//    }
    @GetMapping("/request/{id}")
    public ResponseEntity<BaseResponse<RequestResponse, Void>> getRequestDetail(@PathVariable Long id) {
        return requestService.getEmployeeRequestDetail(id);
    }

    @PostMapping("/request/status/{id}")
    public ResponseEntity<BaseResponse<Void, Void>> updateRequestStatus(@RequestBody StatusDto statusDto, @PathVariable Long id) {
        return requestService.updateRequestStatus(statusDto.getStatus(), id);
    }

    @GetMapping("/request")
    public ResponseEntity<BaseResponse<ListRequestDto, Pageable>> searchRequest(@RequestParam Long userId,
                                                                                @RequestParam String startDate,
                                                                                @RequestParam String endDate,
                                                                                @RequestParam Integer page,
                                                                                @RequestParam Integer limit) throws Exception {
         return requestService.searchRequest(userId,startDate, endDate, page, limit);
    }
}
