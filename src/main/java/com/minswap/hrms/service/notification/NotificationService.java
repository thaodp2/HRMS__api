package com.minswap.hrms.service.notification;

import com.minswap.hrms.entities.Notification;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.NotificationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.List;

public interface NotificationService {

    ResponseEntity<BaseResponse<NotificationResponse, Pagination>> getNotificationsByUserID(Long userID);

    List<Notification> getNotifs(Long userID);

    Flux<ServerSentEvent<List<Notification>>> getNotificationsByUserToID(Long userID);

    void changeNotifStatusToRead(Long notifID);
}
