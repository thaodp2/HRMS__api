package com.minswap.hrms.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;

@RestController
@Validated
public class BorrowHistoryController {
    @Autowired
    private BorrowHistoryService borrowHistoryService;

    @Autowired
    PersonService personService;

    @GetMapping("/borrow-history")
    public ResponseEntity<BaseResponse<BorrowHistoryResponse.BorrowHistoryListResponse, Pageable>> getMyBorrowHistory(
            @RequestParam @Min(1) Integer page,
            @RequestParam @Min(0) Integer limit,
            @RequestParam(required = false) Long deviceTypeId,
            @RequestParam(required = false) Integer isReturned,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String dir,
            @CurrentUser UserPrincipal userPrincipal) {
//        Long personId = Long.valueOf(2);
        Long personId = personService.getPersonInforByEmail(userPrincipal.getEmail()).getPersonId();
        return borrowHistoryService.getBorrowHistoryList(null,personId,page,limit,deviceTypeId,null,sort, dir,isReturned);
    }

    @GetMapping("/borrow-history/{id}")
    public ResponseEntity<BaseResponse<BorrowHistoryResponse, Pageable>> getBorrowHistoryDetail(
            @PathVariable Long id) {
        return borrowHistoryService.getBorrowHistoryDetail(id);
    }
}
