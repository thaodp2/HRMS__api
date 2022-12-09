package com.minswap.hrms.service.timeRemaining;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.EmployeeTimeRemainingRepository;
import com.minswap.hrms.repsotories.OTBudgetRepository;
import com.minswap.hrms.repsotories.RequestTypeRepository;
import com.minswap.hrms.response.EmployeeTimeRemainingResponse;
import com.minswap.hrms.response.dto.EmployeeTimeRemainingDto;
import com.minswap.hrms.response.dto.OTBudgetDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

public class EmployeeTimeRemainingServiceImplTest {
    @Mock
    EmployeeTimeRemainingRepository employeeTimeRemainingRepository;
    @Mock
    OTBudgetRepository otBudgetRepository;
    @Mock
    RequestTypeRepository requestTypeRepository;
    @Mock
    Set<Integer> LEAVE_REQUEST_TYPE;
    @Mock
    Logger log;
    @InjectMocks
    EmployeeTimeRemainingServiceImpl employeeTimeRemainingServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetEmployeeRemainingTime() throws Exception {
        when(employeeTimeRemainingRepository.getLeaveBudgetTimeRemaining(anyLong(), anyLong(), any())).thenReturn(new EmployeeTimeRemainingDto(Double.valueOf(0), Double.valueOf(0)));
        when(otBudgetRepository.getOTBudgetByPersonId(anyLong(), any(), anyInt())).thenReturn(new OTBudgetDto(0d, 0d, 0d, 0d));
        when(requestTypeRepository.getAllRequestTypeId()).thenReturn(List.of(Long.valueOf(1)));

        ResponseEntity<BaseResponse<EmployeeTimeRemainingResponse, Void>> result = employeeTimeRemainingServiceImpl.getEmployeeRemainingTime(Long.valueOf(1), 0, 0, Long.valueOf(1));
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}