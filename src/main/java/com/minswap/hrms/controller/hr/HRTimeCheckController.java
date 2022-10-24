package com.minswap.hrms.controller.hr;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.TimeCheckListResponse;
import com.minswap.hrms.service.timeCheck.TimeCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Pattern;

@RestController
@RequestMapping(CommonConstant.HR + "/")
@Validated
public class HRTimeCheckController {

    @Autowired
    TimeCheckService timeCheckService;

    @GetMapping("/time-check")
    public ResponseEntity<BaseResponse<TimeCheckListResponse, Pageable>> getMySubordinateTimeCheck(@RequestParam Long personId,
                                                                                       @RequestParam (required = false) @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateFrom") String startDate,
                                                                                       @RequestParam (required = false) @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateTo") String endDate,
                                                                                       @RequestParam (defaultValue = "0") Integer page,
                                                                                       @RequestParam (defaultValue = "10") Integer limit) throws Exception {
        return timeCheckService.getMyTimeCheck(personId, startDate, endDate, page, limit);
    }
}