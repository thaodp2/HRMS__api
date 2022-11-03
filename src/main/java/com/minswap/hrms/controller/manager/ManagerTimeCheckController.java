package com.minswap.hrms.controller.manager;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.TimeCheckResponse;
import com.minswap.hrms.service.timeCheck.TimeCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Pattern;
import java.util.List;

@RestController
@RequestMapping(CommonConstant.MANAGER + "/time-check")
public class ManagerTimeCheckController {

    @Autowired
    TimeCheckService timeCheckService;

    @GetMapping("/detail-subordinate")
    public ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachPersonResponse, Pageable>> getDetailSubordinateTimeCheck(@RequestParam Long personId,
                                                                                                                               @RequestParam (required = false) @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateFrom") String startDate,
                                                                                                                               @RequestParam (required = false) @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateTo") String endDate,
                                                                                                                               @RequestParam (defaultValue = "0") Integer page,
                                                                                                                               @RequestParam (defaultValue = "10") Integer limit) throws Exception {
        return timeCheckService.getMyTimeCheck(personId, startDate, endDate, page, limit);
    }

    @GetMapping("/all-subordinate")
    public ResponseEntity<BaseResponse<List<TimeCheckResponse.TimeCheckListSubordinateResponse>, Pageable>> getListSubordinateTimeCheck(@RequestParam (required = false) String search,
                                                                                                                                        @RequestParam (required = false) @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateFrom") String startDate,
                                                                                                                                        @RequestParam (required = false) @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateTo") String endDate,
                                                                                                                                        @RequestParam (defaultValue = "0") Integer page,
                                                                                                                                        @RequestParam (defaultValue = "10") Integer limit) throws Exception {
        return timeCheckService.getListTimeCheck(search, startDate, endDate, page, limit);
    }
}