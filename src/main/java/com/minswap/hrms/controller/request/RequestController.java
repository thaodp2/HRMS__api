package com.minswap.hrms.controller.request;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.EmployeeListResponse;
import com.minswap.hrms.response.RequestDetailRespone;
import com.minswap.hrms.response.dto.ListRequestDto;
import com.minswap.hrms.response.dto.StatusDto;
import com.minswap.hrms.service.RequestDetailService;
import com.minswap.hrms.service.impl.RequestDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(CommonConstant.REQUEST)
public class RequestController {

    @Autowired
    RequestDetailService requestDetailService;

    @GetMapping("")
    public ResponseEntity<BaseResponse<RequestDetailRespone, Void>> getRequestDetail(@RequestParam Long id) {
        return requestDetailService.getEmployeeRequestDetail(id);
    }

    @PostMapping("/status/{id}")
    public ResponseEntity<BaseResponse<Void, Void>> updateRequestStatus(@RequestBody StatusDto statusDto, @PathVariable Long id) {
        return requestDetailService.updateRequestStatus(statusDto.getStatus(), id);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse<ListRequestDto, Pageable>> searchRequest(@RequestParam Long userId,
                                                                                @RequestParam String startDate,
                                                                                @RequestParam String endDate,
                                                                                @RequestParam Integer page,
                                                                                @RequestParam Integer limit) {
       // return requestDetailService.searchRequest(userId,startDate, endDate, page, limit);
        return null;
    }

}
