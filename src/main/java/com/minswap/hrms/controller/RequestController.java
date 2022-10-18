package com.minswap.hrms.controller;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.UpdateStatusRequest;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.response.dto.ListRequestDto;
import com.minswap.hrms.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

@RestController
public class RequestController {
    @Autowired
    private RequestService requestService;

    @GetMapping("request/detail/{id}")
    public ResponseEntity<BaseResponse<RequestResponse, Void>> getRequestDetail(@PathVariable Long id) {
        return requestService.getEmployeeRequestDetail(id);
    }

    @PutMapping("request/status/{id}")
    public ResponseEntity<BaseResponse<Void, Void>> updateRequestStatus(@RequestBody UpdateStatusRequest updateStatusRequest,
                                                                        @PathVariable Long id) {
        return requestService.updateRequestStatus(updateStatusRequest.getStatus(), id);
    }

//    @PutMapping("request/device-request/edit/{id}")
//    public ResponseEntity<BaseResponse<Void, Void>> editBorrowRequest(@RequestBody UpdateStatusRequest updateStatusRequest,
//                                                                      @PathVariable Long id) {
//        return null;
//    }
//
//    @PutMapping("request/leave-benefit-request/edit/{id}")
//    public ResponseEntity<BaseResponse<Void, Void>> editUsualRequest(@RequestBody EditLeaveBenefitRequest editLeaveBenefitRequest,
//                                                                     @PathVariable Long id) {
//        return requestService.editUsualRequest(editLeaveBenefitRequest, id);
//    }

    @GetMapping("/request")
    public ResponseEntity<BaseResponse<ListRequestDto, Pageable>> searchRequest(@RequestParam Long userId,
                                                                                @RequestParam String startDate,
                                                                                @RequestParam String endDate,
                                                                                @RequestParam Integer page,
                                                                                @RequestParam Integer limit) throws Exception {
         return requestService.searchRequest(userId, startDate, endDate, page, limit);
    }
}
