package com.minswap.hrms.controller;

import com.minswap.hrms.entities.Notification;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.NotificationResponse;
import com.minswap.hrms.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@Validated
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("push-notifications/{userID}")
    public Flux<ServerSentEvent<List<Notification>>> streamLastMessage(@PathVariable Long userID) {
        return notificationService.getNotificationsByUserToID(userID);
    }

    @PatchMapping("/read-notifications/{notifID}")
    public ResponseEntity<BaseResponse<HttpStatus, Void>> changeNotifStatusToRead(@PathVariable Long notifID) {
        notificationService.changeNotifStatusToRead(notifID);
        return BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
    }

    @GetMapping("employee/notifications/{userID}")
    public ResponseEntity<BaseResponse<NotificationResponse, Pagination>> getNotificationsByUserID(@PathVariable Long userID) {
        return notificationService.getNotificationsByUserID(userID);
    }

}
