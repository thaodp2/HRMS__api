package com.minswap.hrms.controller.manager;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.TimeCheckResponse;
import com.minswap.hrms.security.UserPrincipal;
import com.minswap.hrms.security.oauth2.CurrentUser;
import com.minswap.hrms.service.person.PersonService;
import com.minswap.hrms.service.timeCheck.TimeCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.List;

@RestController
@RequestMapping(CommonConstant.MANAGER + "/time-check")
public class ManagerTimeCheckController {

    @Autowired
    TimeCheckService timeCheckService;

    @Autowired
    PersonService personService;

    @GetMapping("/detail-subordinate")
    public ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachPersonResponse, Pageable>> getDetailSubordinateTimeCheck(@RequestParam Long personId,
                                                                                                                               @RequestParam @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateFrom") String startDate,
                                                                                                                               @RequestParam @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateTo") String endDate,
                                                                                                                               @RequestParam @Min(1) Integer page,
                                                                                                                               @RequestParam @Min(0)  Integer limit) throws Exception {
        return timeCheckService.getMyTimeCheck(personId, startDate, endDate, page, limit);
    }

    @GetMapping("/all-subordinate")
    public ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachSubordinateResponse, Pageable>> getListSubordinateTimeCheck(@CurrentUser UserPrincipal userPrincipal,
                                                                                                                                  @RequestParam (required = false) String search,
                                                                                                                                  @RequestParam @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateFrom") String startDate,
                                                                                                                                  @RequestParam @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateTo") String endDate,
                                                                                                                                  @RequestParam @Min(1) Integer page,
                                                                                                                                  @RequestParam @Min(0)  Integer limit,
                                                                                                                                  @RequestParam(required = false) String sort,
                                                                                                                                  @RequestParam(required = false) String dir) throws Exception {
        Long managerId = personService.getPersonInforByEmail(userPrincipal.getEmail()).getPersonId();
        return timeCheckService.getListTimeCheck(search, managerId, startDate, endDate, page, limit, sort, dir);
    }
}
