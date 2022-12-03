package com.minswap.hrms.controller.itsupport;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.text.ParseException;

@RestController
@RequestMapping(CommonConstant.ITSUPPORT + "/")
@Validated
public class ITRequestController {

    @Autowired
    private RequestService requestService;

    @GetMapping("/request")
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getAllBorrowDeviceRequest(
            @RequestParam @Min(1) Integer page,
            @RequestParam @Min(0) Integer limit,
            @RequestParam (required = false) String search,
            @RequestParam (required = false) @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid aprrovalDateFrom") String approvalDateFrom,
            @RequestParam (required = false) @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid aprrovalDateTo") String approvalDateTo,
            @RequestParam (required = false) Long deviceTypeId,
            @RequestParam (required = false) String sort,
            @RequestParam (required = false) String dir) throws ParseException {
        return requestService.getBorrowDeviceRequestList(page, limit, search, approvalDateFrom, approvalDateTo, deviceTypeId, sort, dir);
    }
}
