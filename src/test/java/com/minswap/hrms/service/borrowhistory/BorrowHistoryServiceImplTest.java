package com.minswap.hrms.service.borrowhistory;

import com.minswap.hrms.entities.BorrowHistory;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.BorrowHistoryRepository;
import com.minswap.hrms.repsotories.RequestRepository;
import com.minswap.hrms.request.AssignRequest;
import com.minswap.hrms.response.BorrowHistoryResponse;
import com.minswap.hrms.response.dto.BorrowHistoryDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class BorrowHistoryServiceImplTest {
    @Mock
    RequestRepository requestRepository;
    @Mock
    BorrowHistoryRepository borrowHistoryRepository;
    @Mock
    Logger log;
    @InjectMocks
    BorrowHistoryServiceImpl borrowHistoryServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateBorrowHistory() throws Exception {
        Assert.assertThrows(BaseException.class, () ->borrowHistoryServiceImpl.createBorrowHistory(new AssignRequest(Long.valueOf(1), Long.valueOf(1))));
    }

    @Test
    public void testGetBorrowHistoryList1() throws Exception {
        when(borrowHistoryRepository.getBorrowHistoryList(anyString(), anyLong(), anyLong(), anyLong(), anyInt(), any())).thenReturn(new PageImpl<>(Arrays.asList(new BorrowHistoryDto(Long.valueOf(1), "rollNumber", "fullName", "deviceTypeName", "deviceName", "deviceCode", new GregorianCalendar(2022, Calendar.DECEMBER, 8, 16, 36).getTime(), new GregorianCalendar(2022, Calendar.DECEMBER, 8, 16, 36).getTime(), Integer.valueOf(0)))));
        ResponseEntity<BaseResponse<BorrowHistoryResponse.BorrowHistoryListResponse, Pageable>> result = borrowHistoryServiceImpl.getBorrowHistoryList(Long.valueOf(1), Long.valueOf(1), Integer.valueOf(1), Integer.valueOf(10), Long.valueOf(1), "search", "sort", "dir", Integer.valueOf(0));
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testGetBorrowHistoryDetail1() throws Exception {
        when(borrowHistoryRepository.getBorrowHistoryDetail(anyLong())).thenReturn(new BorrowHistoryDto(Long.valueOf(8), "rollNumber", "fullName", "deviceTypeName", "deviceName", "deviceCode", new GregorianCalendar(2022, Calendar.DECEMBER, 8, 16, 36).getTime(), new GregorianCalendar(2022, Calendar.DECEMBER, 8, 16, 36).getTime(), Integer.valueOf(0)));
        ResponseEntity<BaseResponse<BorrowHistoryResponse, Pageable>> result = borrowHistoryServiceImpl.getBorrowHistoryDetail(Long.valueOf(8));
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testGetBorrowHistoryDetail2() throws Exception {
        when(borrowHistoryRepository.getBorrowHistoryDetail(same(Long.valueOf(1)))).thenReturn(new BorrowHistoryDto(Long.valueOf(1), "rollNumber", "fullName", "deviceTypeName", "deviceName", "deviceCode", new GregorianCalendar(2022, Calendar.DECEMBER, 8, 16, 36).getTime(), new GregorianCalendar(2022, Calendar.DECEMBER, 8, 16, 36).getTime(), Integer.valueOf(0)));
        Assert.assertThrows(NumberFormatException.class, () -> borrowHistoryServiceImpl.getBorrowHistoryDetail(Long.valueOf("c")));
    }
}
