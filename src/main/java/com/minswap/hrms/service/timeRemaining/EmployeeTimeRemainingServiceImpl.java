package com.minswap.hrms.service.timeRemaining;

import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.EmployeeTimeRemainingRepository;
import com.minswap.hrms.repsotories.OTBudgetRepository;
import com.minswap.hrms.request.TimeRemainingRequest;
import com.minswap.hrms.response.EmployeeTimeRemainingResponse;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.response.dto.EmployeeTimeRemainingDto;
import com.minswap.hrms.response.dto.OTBudgetDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class EmployeeTimeRemainingServiceImpl implements EmployeeTimeRemainingService {

    @Autowired
    EmployeeTimeRemainingRepository employeeTimeRemainingRepository;

    @Autowired
    OTBudgetRepository otBudgetRepository;

    /*
        ANNUAL_LEAVE_TYPE_ID = 1;
        CHILDREN_SICKNESS = 3;
        UNPAID_LEAVE = 6;
        SICK_LEAVE = 8;
        BEREAVEMENT_LEAVE = 10;
     */
    private static final Set<Integer> LEAVE_REQUEST_TYPE = new HashSet<Integer>(Arrays.asList(1, 3, 6, 8, 10));

    private static final int OT_TYPE_ID = 7;


    @Override
    public ResponseEntity<BaseResponse<EmployeeTimeRemainingResponse, Void>> getEmployeeRemainingTime(TimeRemainingRequest timeRemainingRequest) {
        ResponseEntity<BaseResponse<EmployeeTimeRemainingResponse, Void>> responseEntity = null;
        EmployeeTimeRemainingResponse employeeTimeRemainingResponse = new EmployeeTimeRemainingResponse();
        Year year = Year.of(timeRemainingRequest.getYear());
        int month = timeRemainingRequest.getMonth();
        Long requestTypeId = timeRemainingRequest.getRequestTypeId();
        if (requestTypeId == OT_TYPE_ID) {
            if (Integer.valueOf(month) == null) {
                throw new BaseException(ErrorCode.MONTH_INVALID);
            }
            else if (year == null) {
                throw new BaseException(ErrorCode.YEAR_INVALID);
            }
            OTBudgetDto otBudgetDto = otBudgetRepository.getOTBudgetByPersonId(Long.valueOf(2),
                                                                               year,
                                                                               month);
            if (otBudgetDto == null) {
                throw new BaseException(ErrorCode.RESULT_NOT_FOUND);
            } else {
                double timeRemaining = otBudgetDto.getOtHoursBudget() - otBudgetDto.getHoursWorked();
                EmployeeTimeRemainingDto employeeTimeRemainingDto = new EmployeeTimeRemainingDto(timeRemaining);
                employeeTimeRemainingResponse = new EmployeeTimeRemainingResponse(employeeTimeRemainingDto);
                responseEntity = BaseResponse.ofSucceededOffset(employeeTimeRemainingResponse, null);
            }
        } else if (LEAVE_REQUEST_TYPE.contains(requestTypeId.intValue())) {
            if (year == null) {
                throw new BaseException(ErrorCode.YEAR_INVALID);
            }
            EmployeeTimeRemainingDto employeeTimeRemainingDto =
                    employeeTimeRemainingRepository.getTimeRemaining(Long.valueOf(2),
                                                                    requestTypeId,
                                                                    year);
            if (employeeTimeRemainingDto == null) {
                throw new BaseException(ErrorCode.RESULT_NOT_FOUND);
            } else {
                employeeTimeRemainingResponse = new EmployeeTimeRemainingResponse(employeeTimeRemainingDto);
                responseEntity = BaseResponse.ofSucceededOffset(employeeTimeRemainingResponse, null);
            }
        } else {
            responseEntity = BaseResponse.ofSucceededOffset(employeeTimeRemainingResponse, null);
        }

        return responseEntity;
    }
}
