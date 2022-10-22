package com.minswap.hrms.controller.manager;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.text.ParseException;

@RestController
@RequestMapping(CommonConstant.MANAGER + "/")
@Validated
public class ManagerRequestController {
    @Autowired
    private RequestService requestService;

    @GetMapping("/request")
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getSubordinateRequest(
            @RequestParam Integer page,
            @RequestParam Integer limit,
            @RequestParam (required = false) @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateFrom") String createDateFrom,
            @RequestParam (required = false) @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateTo") String createDateTo,
            @RequestParam (required = false) Long requestTypeId) throws ParseException {
        Long managerId = Long.valueOf(5);
        if(createDateFrom == null && createDateTo == null && requestTypeId == null){
            return requestService.getSubordinateRequest(managerId,page,limit,false,createDateFrom,createDateTo,requestTypeId);
        }else {
            return requestService.getSubordinateRequest(managerId,page,limit,true,createDateFrom,createDateTo,requestTypeId);
        }
    }


}
