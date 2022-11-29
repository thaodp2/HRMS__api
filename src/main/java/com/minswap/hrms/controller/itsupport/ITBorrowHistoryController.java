package com.minswap.hrms.controller.itsupport;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.BorrowHistoryResponse;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.service.borrowhistory.BorrowHistoryService;
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
public class ITBorrowHistoryController {
    @Autowired
    private BorrowHistoryService borrowHistoryService;

    @GetMapping("/borrow-history")
    public ResponseEntity<BaseResponse<BorrowHistoryResponse.BorrowHistoryListResponse, Pageable>> getAllBorrowHistory(
            @RequestParam @Min(1) Integer page,
            @RequestParam @Min(0) Integer limit,
            @RequestParam(required = false) Long deviceTypeId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer isReturned,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String dir) {
        return borrowHistoryService.getBorrowHistoryList(null,null,page,limit,deviceTypeId,search,sort, dir,isReturned);
    }
}
