package com.minswap.hrms.service.notification;

import com.minswap.hrms.entities.Notification;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.NotificationResponse;
import com.minswap.hrms.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface NotificationService {

    void changeNotifStatusToRead(UserPrincipal user, Long notifID);

    ResponseEntity<BaseResponse<NotificationResponse, Pagination>> getNotificationsByUserID(Integer page, Integer limit,Long userID);

    NotificationResponse getNotifs(Long userID);

    ResponseEntity<BaseResponse<NotificationResponse, Pagination>> getTotalUnreadNotifs(Long userID);

    Flux<ServerSentEvent<NotificationResponse>> getNotificationsByUserToID(Long userID);

    void send(Notification... notif);
}
