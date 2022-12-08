package com.minswap.hrms.service.benefitbudget;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.LeaveBudgetRepository;
import com.minswap.hrms.repsotories.OTBudgetRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.response.BenefitBudgetResponse;
import com.minswap.hrms.response.dto.BenefitBudgetDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityManager;
import java.time.Year;
import java.util.List;

import static org.mockito.Mockito.*;

public class BenefitBudgetServiceImplTest {
    @Mock
    LeaveBudgetRepository leaveBudgetRepository;
    @Mock
    OTBudgetRepository otBudgetRepository;
    @Mock
    PersonRepository personRepository;
    @Mock
    EntityManager entityManager;
    @Mock
    Logger log;
    @InjectMocks
    BenefitBudgetServiceImpl benefitBudgetServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetBenefitBudget1() throws Exception {
        when(leaveBudgetRepository.getBenefitBudgetListWithoutPaging(any(), anyString(), anyLong(), anyLong(), anyLong(), any())).thenReturn(List.of(new BenefitBudgetDto(Long.valueOf(1), "rollNumber", "fullName", Double.valueOf(0), Double.valueOf(0), Double.valueOf(0), Double.valueOf(0), "requestTypeName")));
        ResponseEntity<BaseResponse<BenefitBudgetResponse.BenefitBudgetListResponse, Pageable>> result = benefitBudgetServiceImpl.getBenefitBudget(null, null, null, null, Long.valueOf(1), null, null, Year.of(2022), null, null);
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testGetBenefitBudget2() throws Exception {
        when(otBudgetRepository.getBenefitBudgetListWithoutPaging(anyInt(), any(), anyString(), anyLong(), anyLong(), any())).thenReturn(List.of(new BenefitBudgetDto(Long.valueOf(1), "rollNumber", "fullName", Double.valueOf(0), Double.valueOf(0), Double.valueOf(0), Double.valueOf(0), "requestTypeName")));
        ResponseEntity<BaseResponse<BenefitBudgetResponse.BenefitBudgetListResponse, Pageable>> result = benefitBudgetServiceImpl.getBenefitBudget(null, null, null, null, Long.valueOf(7), null, null, Year.of(2022), null, null);
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }


}

