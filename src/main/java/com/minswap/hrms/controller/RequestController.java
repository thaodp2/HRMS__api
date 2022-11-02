package com.minswap.hrms.controller;

import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.*;
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
import javax.validation.constraints.Pattern;
import java.text.ParseException;

@RestController
@Validated
public class RequestController {
    @Autowired
    private RequestService requestService;

    @GetMapping("request/{id}")
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
                                                                        @PathVariable Long id) throws ParseException {
        return requestService.updateRequestStatus(updateStatusRequest.getStatus(), id);
    }

    @PutMapping("request/edit/{id}")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<Void, Void>> editRequest(@RequestBody
                                                                @Valid
                                                                EditRequest editRequest,
                                                                BindingResult bindingResult,
                                                                @PathVariable Long id) {
        return requestService.editRequest(editRequest, id);
    }

    @GetMapping("/request")
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getMyRequest(
            @RequestParam @Min(1) Integer page,
            @RequestParam @Min(0) Integer limit,
            @RequestParam (required = false) @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateFrom") String createDateFrom,
            @RequestParam (required = false) @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateTo") String createDateTo,
            @RequestParam (required = false) Long requestTypeId) throws ParseException {
        Long id = Long.valueOf(2);
        if(createDateFrom == null && createDateTo == null && requestTypeId == null){
            return requestService.getMyRequest(id,page,limit,false,createDateFrom,createDateTo,requestTypeId);
        }else {
            return requestService.getMyRequest(id,page,limit,true,createDateFrom,createDateTo,requestTypeId);
        }
    }

    @DeleteMapping("/request/{id}")
    public ResponseEntity<BaseResponse<Void, Void>> cancelRequest(@PathVariable Long id) {
        return requestService.cancelRequest(id);
    }
}
