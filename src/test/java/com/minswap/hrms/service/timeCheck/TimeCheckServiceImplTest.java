package com.minswap.hrms.service.timeCheck;

import com.minswap.hrms.configuration.AppConfig;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.OfficeTimeRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.repsotories.SignatureProfileRepository;
import com.minswap.hrms.repsotories.TimeCheckRepository;
import com.minswap.hrms.request.TimeCheckInRequest;
import com.minswap.hrms.response.TimeCheckResponse;
import com.minswap.hrms.response.dto.DailyTimeCheckDto;
import com.minswap.hrms.response.dto.TimeCheckEachSubordinateDto;
import com.minswap.hrms.service.request.RequestServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.DateFormat;
import java.util.*;

import static org.mockito.Mockito.*;

public class TimeCheckServiceImplTest {
    @Mock
    DateFormat DATE_FORMAT;
    @Mock
    DateFormat TIME_EXCLUDED_DATE_FORMAT;
    @Mock
    AppConfig appConfig;
    @Mock
    TimeCheckRepository timeCheckRepository;
    @Mock
    RequestServiceImpl requestService;
    @Mock
    PersonRepository personRepository;
    @Mock
    SignatureProfileRepository signatureProfileRepository;
    @Mock
    OfficeTimeRepository officeTimeRepository;
    @InjectMocks
    TimeCheckServiceImpl timeCheckServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }







    @Test
    public void testIsValidHeaderTemplate() throws Exception {
        boolean result = timeCheckServiceImpl.isValidHeaderTemplate(null);
        Assert.assertEquals(false, result);
    }

    @Test
    public void testImportExcel() throws Exception {
        when(appConfig.getMillisecondSevenHours()).thenReturn(0L);
        when(timeCheckRepository.getDailyTimeCheck(anyLong(), any())).thenReturn(new DailyTimeCheckDto(new GregorianCalendar(2022, Calendar.DECEMBER, 27, 0, 0).getTime(), new GregorianCalendar(2022, Calendar.DECEMBER, 27, 0, 0).getTime(), Double.valueOf(0), Double.valueOf(0)));
        when(timeCheckRepository.updateTimeCheck(any(), anyDouble(), anyDouble(), anyLong())).thenReturn(Integer.valueOf(0));
        when(requestService.calculateNumOfHoursWorkedInADay(any(), any())).thenReturn(0d);
        when(signatureProfileRepository.findSignatureProfileByPrivateKeySignature(anyString())).thenReturn(null);
        when(officeTimeRepository.findOfficeTimeByOfficeTimeId(anyLong())).thenReturn(null);

        ResponseEntity<BaseResponse<HttpStatus, Void>> result = timeCheckServiceImpl.importExcel(null);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }


    @Test
    public void testGetStringDateFromDateTime() throws Exception {
        String result = timeCheckServiceImpl.getStringDateFromDateTime(new GregorianCalendar(2022, Calendar.DECEMBER, 27, 0, 0).getTime());
        Assert.assertEquals("2022-12-27", result);
    }
}

