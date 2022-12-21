package com.minswap.hrms.service.payroll;

import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.Role;
import com.minswap.hrms.entities.Salary;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.NotificationRepository;
import com.minswap.hrms.repsotories.PayrollRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.response.PayrollResponse;
import com.minswap.hrms.security.UserPrincipal;
import com.minswap.hrms.service.email.EmailSenderService;
import com.minswap.hrms.service.person.PersonService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.mockito.Mockito.*;

public class PayrollServiceImplTest {
    @Mock
    PayrollRepository payrollRepository;
    @Mock
    PersonRepository personRepository;
    @Mock
    EmailSenderService emailSenderService;
    @Mock
    NotificationRepository notificationRepository;
    @Mock
    PersonService personService;
    @InjectMocks
    PayrollServiceImpl payrollServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSendPayrollToEmail() throws Exception {
        when(payrollRepository.findByPersonIdAndMonthAndYear(anyLong(), anyInt(), any())).thenReturn(Optional.of(new Salary()));
        when(personService.getPersonInforByEmail(anyString())).thenReturn(new Person(Long.valueOf(1), "fullName", "address", "citizenIdentification", "phoneNumber", "email", new GregorianCalendar(2022, Calendar.DECEMBER, 8, 19, 31).getTime(), Long.valueOf(1), Long.valueOf(1), Long.valueOf(1), 0, "rollNumber", new GregorianCalendar(2022, Calendar.DECEMBER, 8, 19, 31).getTime(), "status", Long.valueOf(1), Double.valueOf(0), Double.valueOf(0), "avatarImg", Double.valueOf(0), "pinCode"));

        ResponseEntity<BaseResponse<HttpStatus, Void>> result = payrollServiceImpl.sendPayrollToEmail(new UserPrincipal("email", "name", List.of(new Role(Long.valueOf(1), "roleName"))), 0, 0, "111qqq12");
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}

