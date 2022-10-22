package com.minswap.hrms.service.timeCheck;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.TimeCheckListResponse;
import com.minswap.hrms.response.dto.ListRequestDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface TimeCheckService {

    ResponseEntity<BaseResponse<TimeCheckListResponse, Pageable>> getMyTimeCheck(Long personId, String startDate, String endDate,
                                                                                 Integer page, Integer limit) throws Exception;

}
