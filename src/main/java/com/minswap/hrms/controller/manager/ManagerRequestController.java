package com.minswap.hrms.controller.manager;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(CommonConstant.MANAGER + "/")
public class ManagerRequestController {
    @Autowired
    private RequestService requestService;


//    @GetMapping("/{managerId}/request")
//    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getListEmployee(@PathVariable Integer managerId, @RequestParam int page, @RequestParam int limit) {
//        return requestService.getSubordinateRequest(managerId,page, limit);
//    }
}
