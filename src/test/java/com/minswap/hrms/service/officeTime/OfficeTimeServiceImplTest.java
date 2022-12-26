package com.minswap.hrms.service.officeTime;

import com.minswap.hrms.entities.OfficeTime;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.NotificationRepository;
import com.minswap.hrms.repsotories.OfficeTimeRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.request.OfficeTimeRequest;
import com.minswap.hrms.response.OfficeTimeResponse;
import com.minswap.hrms.response.dto.OfficeTimeDto;
import com.minswap.hrms.service.notification.NotificationService;
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

public class OfficeTimeServiceImplTest {
    @Mock
    PersonRepository personRepository;
    @Mock
    NotificationRepository notificationRepository;
    @Mock
    OfficeTimeRepository officeTimeRepository;
    @Mock
    NotificationService notificationService;
    @InjectMocks
    OfficeTimeServiceImpl officeTimeServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdateOfficeTime() throws Exception {
        when(personRepository.getAllPersonId()).thenReturn(List.of(Long.valueOf(1)));
        when(officeTimeRepository.getPresentOfficeTimeId()).thenReturn(Long.valueOf(1));
        when(officeTimeRepository.findOfficeTimeByOfficeTimeId(anyLong())).thenReturn(Optional.of(new OfficeTime(1L, "08:30:00", "15:00:00", new Date(), "08:30:00", "15:00:00")));

        ResponseEntity<BaseResponse<HttpStatus, Void>> result = officeTimeServiceImpl.updateOfficeTime(new OfficeTimeRequest("08:30:00", "15:00:00"));
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testGetOfficeTime() throws Exception {
        when(officeTimeRepository.getPresentOfficeTimeId()).thenReturn(Long.valueOf(1));
        when(officeTimeRepository.findOfficeTimeByOfficeTimeId(anyLong())).thenReturn(Optional.of(new OfficeTime(1L,"12","12",new Date(),"12","12")));

        ResponseEntity<BaseResponse<OfficeTimeResponse, Void>> result = officeTimeServiceImpl.getOfficeTime();
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testUpdateLunchBreak() throws Exception {
        when(personRepository.getAllPersonId()).thenReturn(List.of(Long.valueOf(1)));
        when(officeTimeRepository.getOfficeTime()).thenReturn(new OfficeTimeDto("08:30:00", "15:00:00", null, null));
        when(officeTimeRepository.updateLunchBreakTime(anyString(), anyString(), any())).thenReturn(Integer.valueOf(0));

        ResponseEntity<BaseResponse<HttpStatus, Void>> result = officeTimeServiceImpl.updateLunchBreak(new OfficeTimeRequest("08:30:00", "15:00:00"));
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }


    @Test
    public void testCalculateHoursBetweenTwoDateTime() throws Exception {
        double result = officeTimeServiceImpl.calculateHoursBetweenTwoDateTime(new GregorianCalendar(2022, Calendar.DECEMBER, 26, 23, 32).getTime(), new GregorianCalendar(2022, Calendar.DECEMBER, 26, 23, 32).getTime());
        Assert.assertEquals(0d, result,0d);
    }

}

