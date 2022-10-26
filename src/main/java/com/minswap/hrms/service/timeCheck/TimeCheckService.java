package com.minswap.hrms.service.timeCheck;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.TimeCheckResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TimeCheckService {

    ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachPersonResponse, Pageable>> getMyTimeCheck(Long personId, String startDate, String endDate,
                                                                                                         Integer page, Integer limit) throws Exception;

    ResponseEntity<BaseResponse<List<TimeCheckResponse.TimeCheckListSubordinateResponse>, Pageable>> getListTimeCheck(String search, String startDate,
                                                                                                                      String endDate, Integer page,
                                                                                                                      Integer limit) throws Exception;

}
