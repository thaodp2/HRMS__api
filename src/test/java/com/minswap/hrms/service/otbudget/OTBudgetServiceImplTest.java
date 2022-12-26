package com.minswap.hrms.service.otbudget;

import com.minswap.hrms.repsotories.OTBudgetRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import static org.mockito.Mockito.*;

public class OTBudgetServiceImplTest {
    @Mock
    OTBudgetRepository otBudgetRepository;
    @Mock
    PersonRepository personRepository;
    @Mock
    Logger log;
    @InjectMocks
    OTBudgetServiceImpl oTBudgetServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateOTBudgetEachMonth() throws Exception {
        when(otBudgetRepository.findByPersonIdAndMonthAndYear(anyLong(), anyInt(), any())).thenReturn(null);

        oTBudgetServiceImpl.createOTBudgetEachMonth();
    }
}

