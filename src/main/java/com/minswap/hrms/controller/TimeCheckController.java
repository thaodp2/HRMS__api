package com.minswap.hrms.controller;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.SignatureProfileRequest;
import com.minswap.hrms.request.TimeCheckInRequest;
import com.minswap.hrms.response.TimeCheckResponse;
import com.minswap.hrms.security.UserPrincipal;
import com.minswap.hrms.security.oauth2.CurrentUser;
import com.minswap.hrms.service.person.PersonService;
import com.minswap.hrms.service.timeCheck.TimeCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@RestController
@Validated
@RequestMapping(TimeCheckController.TIME_CHECK)
public class TimeCheckController {
    public static final String TIME_CHECK = "/time-check";
    @Autowired
    TimeCheckService timeCheckService;

    @Autowired
    PersonService personService;
    @GetMapping("")
    public ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachPersonResponse, Pageable>> getMyTimeCheck(@CurrentUser UserPrincipal userPrincipal,
                                                                                                                @RequestParam @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateFrom") String startDate,
                                                                                                                @RequestParam @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateTo") String endDate,
                                                                                                                @RequestParam @Min(1)Integer page,
                                                                                                                @RequestParam @Min(0) Integer limit) throws Exception {
        Long personId = personService.getPersonInforByEmail(userPrincipal.getEmail()).getPersonId();
        return timeCheckService.getMyTimeCheck(personId, startDate, endDate, page, limit);
    }
    @PostMapping("")
    public ResponseEntity<BaseResponse<Void, Void>> logTimeCheck(
            @RequestBody TimeCheckInRequest timeCheckInRequest) {

        return timeCheckService.logTimeCheck(timeCheckInRequest);
    }
}
