package com.minswap.hrms.controller;

import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.EditDeviceRequest;
import com.minswap.hrms.request.EditLeaveBenefitRequest;
import com.minswap.hrms.request.UpdateStatusRequest;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.response.dto.ListRequestDto;
import com.minswap.hrms.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@RestController
@Validated
public class RequestController {
    @Autowired
    private RequestService requestService;

    @GetMapping("request/detail/{id}")
    public ResponseEntity<BaseResponse<RequestResponse, Void>> getRequestDetail(
            @PathVariable
            @Min(value = 1, message = "ID must be greater or equal 1")
            Long id) {
        return requestService.getEmployeeRequestDetail(id);
    }

    @PutMapping("request/status/{id}")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<Void, Void>> updateRequestStatus(@RequestBody
                                                                        @Valid UpdateStatusRequest updateStatusRequest,
                                                                        BindingResult bindingResult,
                                                                        @PathVariable Long id) {
        return requestService.updateRequestStatus(updateStatusRequest.getStatus(), id);
    }

    @PutMapping("request/device-request/edit/{id}")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<Void, Void>> editBorrowRequest(@RequestBody
                                                                      @Valid
                                                                      EditDeviceRequest editDeviceRequest,
                                                                      BindingResult bindingResult,
                                                                      @PathVariable Long id
                                                                      ) {
        return  requestService.editDeviceRequest(editDeviceRequest, id);
    }

    @PutMapping("request/leave-benefit-request/edit/{id}")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<Void, Void>> editLeaveBenefitRequest(@RequestBody
                                                                            @Valid
                                                                            EditLeaveBenefitRequest editLeaveBenefitRequest,
                                                                            BindingResult bindingResult,
                                                                            @PathVariable Long id) {
        return requestService.editLeaveBenefitRequest(editLeaveBenefitRequest, id);
    }

    @GetMapping("/request")
    public ResponseEntity<BaseResponse<ListRequestDto, Pageable>> searchRequest(@RequestParam Long userId,
                                                                                @RequestParam String startDate,
                                                                                @RequestParam String endDate,
                                                                                @RequestParam Integer page,
                                                                                @RequestParam Integer limit) throws Exception {
         return requestService.searchRequest(userId, startDate, endDate, page, limit);
    }
}
