package com.minswap.hrms.controller.manager;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.BorrowHistoryResponse;
import com.minswap.hrms.security.UserPrincipal;
import com.minswap.hrms.security.oauth2.CurrentUser;
import com.minswap.hrms.service.borrowhistory.BorrowHistoryService;
import com.minswap.hrms.service.person.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;

@RestController
@RequestMapping(CommonConstant.MANAGER + "/")
@Validated
public class ManagerBorrowHistoryController {
    @Autowired
    private BorrowHistoryService borrowHistoryService;

    @Autowired
    PersonService personService;

    @GetMapping("/borrow-history")
    public ResponseEntity<BaseResponse<BorrowHistoryResponse.BorrowHistoryListResponse, Pageable>> getAllBorrowHistory(
            @RequestParam @Min(1) Integer page,
            @RequestParam @Min(0) Integer limit,
            @RequestParam(required = false) Long deviceTypeId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String dir,
            @CurrentUser UserPrincipal userPrincipal) {
//        Long managerId = Long.valueOf(5);
        Long managerId = personService.getPersonInforByEmail(userPrincipal.getEmail()).getPersonId();
        return borrowHistoryService.getBorrowHistoryList(managerId,null,page,limit,deviceTypeId,search,sort, dir, status);
    }
}
