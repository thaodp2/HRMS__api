package com.minswap.hrms.controller;

import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.CreateRequest;
import com.minswap.hrms.request.EditRequest;
import com.minswap.hrms.request.UpdateStatusRequest;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("request/{id}")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<Void, Void>> editRequest(@RequestBody
                                                                @Valid
                                                                EditRequest editRequest,
                                                                BindingResult bindingResult,
                                                                @PathVariable Long id) throws ParseException {
        return requestService.editRequest(editRequest, id);
    }

    @GetMapping("/request")
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getMyRequest(
            @RequestParam @Min(1) Integer page,
            @RequestParam @Min(0) Integer limit,
            @RequestParam (required = false) @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateFrom") String createDateFrom,
            @RequestParam (required = false) @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateTo") String createDateTo,
            @RequestParam (required = false) Long requestTypeId,
            @RequestParam (required = false) String status,
            @RequestParam (required = false) String sort,
            @RequestParam (required = false) String dir) throws ParseException {
        Long id = Long.valueOf(2);
            return requestService.getMyRequest(id,page,limit,null,createDateFrom,createDateTo,requestTypeId, status, sort, dir);
    }

    @PostMapping("/request")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<Void, Void>> createRequest(@RequestBody @Valid CreateRequest createRequest,
                                                                  BindingResult bindingResult) throws ParseException {
        return requestService.createRequest(createRequest);
    }

    @PutMapping("/request/cancel-request/{id}")
    public  ResponseEntity<BaseResponse<Void, Void>> cancelRequest(@PathVariable Long id) {
        return requestService.cancelRequest(id);
    }
}
