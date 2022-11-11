package com.minswap.hrms.service.timeRemaining;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.TimeRemainingRequest;
import com.minswap.hrms.response.EmployeeTimeRemainingResponse;
import org.springframework.http.ResponseEntity;

public interface EmployeeTimeRemainingService {
    ResponseEntity<BaseResponse<EmployeeTimeRemainingResponse, Void>> getEmployeeRemainingTime
                                                                (Long requestTypeId,
                                                                 int month,
                                                                 int year);

}
