package com.minswap.hrms.service.notification;

import com.minswap.hrms.entities.Notification;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.NotificationResponse;
import com.minswap.hrms.security.UserPrincipal;
import org.springframework.http.ResponseEntity;

public interface NotificationService {

    void changeNotifStatusToRead(Long notifID);

    ResponseEntity<BaseResponse<NotificationResponse, Pagination>> getNotificationsByUserID(Integer page, Integer limit,Long userID);

    ResponseEntity<BaseResponse<Long, Pagination>> getTotalUnreadNotifs(Long userID);


    void send(Notification... notif);
}
