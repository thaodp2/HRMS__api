package com.minswap.hrms.service.leavebudget;

import com.minswap.hrms.entities.LeaveBudget;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.repsotories.LeaveBudgetRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.time.Year;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class LeaveBudgetServiceImplTest {
    @Mock
    LeaveBudgetRepository leaveBudgetRepository;
    @Mock
    PersonRepository personRepository;
    @Mock
    Logger log;
    @InjectMocks
    LeaveBudgetServiceImpl leaveBudgetServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateLeaveBudget() throws Exception {
        when(leaveBudgetRepository.findByPersonIdAndYear(anyLong(), any())).thenReturn(Optional.of(new LeaveBudget(Long.valueOf(1), 50, 0.5, 40, Year.of(2022), Long.valueOf(6))));
        when(personRepository.findByRankIdIsNot(anyLong())).thenReturn(List.of(new Person(Long.valueOf(1), "fullName", "address", "citizenIdentification", "phoneNumber", "email", new GregorianCalendar(2022, Calendar.DECEMBER, 26, 23, 28).getTime(), Long.valueOf(1), Long.valueOf(1), Long.valueOf(1), 0, "rollNumber", new GregorianCalendar(2022, Calendar.DECEMBER, 26, 23, 28).getTime(), "status", Long.valueOf(1), Double.valueOf(0), Double.valueOf(0), "avatarImg", Double.valueOf(0), "pinCode")));

        leaveBudgetServiceImpl.createLeaveBudget();
    }

    @Test
    public void testUpdateLeaveBudgetEachMonth() throws Exception {
        when(leaveBudgetRepository.findByPersonIdAndYearAndRequestTypeId(anyLong(), any(), anyLong())).thenReturn(Optional.of(new LeaveBudget(Long.valueOf(1), 50, 0.5, 40, Year.of(2022), Long.valueOf(6))));
        when(personRepository.findByRankIdIsNot(anyLong())).thenReturn(List.of(new Person(Long.valueOf(1), "fullName", "address", "citizenIdentification", "phoneNumber", "email", new GregorianCalendar(2022, Calendar.DECEMBER, 26, 23, 28).getTime(), Long.valueOf(1), Long.valueOf(1), Long.valueOf(1), 0, "rollNumber", new GregorianCalendar(2022, Calendar.DECEMBER, 26, 23, 28).getTime(), "status", Long.valueOf(1), Double.valueOf(0), Double.valueOf(0), "avatarImg", Double.valueOf(0), "pinCode")));

        leaveBudgetServiceImpl.updateLeaveBudgetEachMonth();
    }
}
