package com.minswap.hrms.service.notification;

import com.minswap.hrms.entities.Notification;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.NotificationRepository;
import com.minswap.hrms.response.NotificationResponse;
import com.minswap.hrms.response.dto.NotificationDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.mockito.Mockito.*;

public class NotificationServiceImplTest {
    @Mock
    NotificationRepository notificationRepository;
    @Mock
    Logger log;
    @InjectMocks
    NotificationServiceImpl notificationServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetNotificationsByUserID1() throws Exception {
        when(notificationRepository.getMyNotifications(anyLong(), any())).thenReturn(null);
        ResponseEntity<BaseResponse<NotificationResponse, Pagination>> result = notificationServiceImpl.getNotificationsByUserID(Integer.valueOf(1), Integer.valueOf(10), Long.valueOf(1));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testGetNotificationsByUserID2() throws Exception {
        when(notificationRepository.getMyNotifications(anyLong(), any())).thenReturn(new PageImpl<>(List.of(new NotificationDto())));
        ResponseEntity<BaseResponse<NotificationResponse, Pagination>> result = notificationServiceImpl.getNotificationsByUserID(Integer.valueOf(1), Integer.valueOf(10), Long.valueOf(1));
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testGetTotalUnreadNotifs() throws Exception {
        when(notificationRepository.findByUserToAndIsRead(anyLong(), anyInt())).thenReturn(List.of(new Notification(Long.valueOf(1), Long.valueOf(1), Long.valueOf(1), "content", "redirectUrl", Integer.valueOf(0), Integer.valueOf(0), new GregorianCalendar(2022, Calendar.DECEMBER, 8, 18, 35).getTime())));

        ResponseEntity<BaseResponse<NotificationResponse, Pagination>> result = notificationServiceImpl.getTotalUnreadNotifs(Long.valueOf(1));
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testGetTotal() throws Exception {
        when(notificationRepository.findByUserToAndIsRead(anyLong(), anyInt())).thenReturn(List.of(new Notification(Long.valueOf(1), Long.valueOf(1), Long.valueOf(1), "content", "redirectUrl", Integer.valueOf(0), Integer.valueOf(0), new GregorianCalendar(2022, Calendar.DECEMBER, 8, 18, 35).getTime())));

        Long result = notificationServiceImpl.getTotal(Long.valueOf(1));
        Assert.assertEquals(Long.valueOf(1), result);
    }


    @Test
    public void testChangeNotifStatusToRead() throws Exception {
        notificationServiceImpl.changeNotifStatusToRead(Long.valueOf(1));
    }
}

