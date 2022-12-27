package com.minswap.hrms.service.request;

import com.minswap.hrms.configuration.AppConfig;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.*;
import com.minswap.hrms.request.CreateRequest;
import com.minswap.hrms.request.EditRequest;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.response.dto.*;
import com.minswap.hrms.service.notification.NotificationService;
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
import java.util.*;

import static org.mockito.Mockito.*;

public class RequestServiceImplTest {
    @Mock
    RequestRepository requestRepository;
    @Mock
    LeaveBudgetRepository leaveBudgetRepository;
    @Mock
    OTBudgetRepository otBudgetRepository;
    @Mock
    EntityManager entityManager;
    @Mock
    RequestTypeRepository requestTypeRepository;
    @Mock
    DeviceTypeRepository deviceTypeRepository;
    @Mock
    EvidenceRepository evidenceRepository;
    @Mock
    OfficeTimeRepository officeTimeRepository;
    @Mock
    TimeCheckRepository timeCheckRepository;
    @Mock
    PersonRepository personRepository;
    @Mock
    NotificationRepository notificationRepository;
    @Mock
    Set<Integer> LEAVE_REQUEST_TYPE;
    @Mock
    AppConfig appConfig;
    @Mock
    NotificationService notificationService;
    @Mock
    Logger log;
    @InjectMocks
    RequestServiceImpl requestServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCalculateHoursBetweenTwoDateTime() throws Exception {
        double result = requestServiceImpl.calculateHoursBetweenTwoDateTime(new GregorianCalendar(2022, Calendar.DECEMBER, 27, 0, 11).getTime(), new GregorianCalendar(2022, Calendar.DECEMBER, 27, 0, 11).getTime());
        Assert.assertEquals(0d, result,0d);
    }

    @Test
    public void testGetStringDateFromDateTime() throws Exception {
        String result = requestServiceImpl.getStringDateFromDateTime(new GregorianCalendar(2022, Calendar.DECEMBER, 27, 0, 11).getTime());
        Assert.assertEquals("2022-12-27", result);
    }
    @Test
    public void testGetDayOfDate() throws Exception {
        int result = requestServiceImpl.getDayOfDate(new GregorianCalendar(2022, Calendar.DECEMBER, 27, 0, 11).getTime());
        Assert.assertEquals(27, result);
    }
    @Test
    public void testGetMonthOfDate() throws Exception {
        int result = requestServiceImpl.getMonthOfDate(new GregorianCalendar(2022, Calendar.DECEMBER, 27, 0, 11).getTime());
        Assert.assertEquals(12, result);
    }

    @Test
    public void testValidateOTTime() throws Exception {
        requestServiceImpl.validateOTTime(0d, 0d, 0d, 0d);
    }

    @Test
    public void testGetNotiContentWhenCreateRequest() throws Exception {
        String result = requestServiceImpl.getNotiContentWhenCreateRequest();
        Assert.assertEquals("send you a request", result);
    }

    @Test
    public void testGetNotiContentWhenUpdateRequestStatus() throws Exception {
        String result = requestServiceImpl.getNotiContentWhenUpdateRequestStatus("requestTypeName");
        Assert.assertEquals("has processed your requestTypeName request", result);
    }

    @Test
    public void testGetNotiRequestUrlForViewDetail() throws Exception {
        String result = requestServiceImpl.getNotiRequestUrlForViewDetail(Long.valueOf(1));
        Assert.assertEquals("emp-self-service/request/detail/1", result);
    }

    @Test
    public void testGetNotiRequestUrlForUpdateStatus() throws Exception {
        String result = requestServiceImpl.getNotiRequestUrlForUpdateStatus("type", Long.valueOf(1));
        Assert.assertEquals("request-center/type/detail/1", result);
    }

    @Test
    public void testGetNotiURLForITSupport() throws Exception {
        String result = requestServiceImpl.getNotiURLForITSupport();
        Assert.assertEquals("request-center/borrow-device", result);
    }

}

