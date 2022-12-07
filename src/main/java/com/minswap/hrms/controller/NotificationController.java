package com.minswap.hrms.controller;

import com.minswap.hrms.entities.Notification;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.NotificationResponse;
import com.minswap.hrms.response.dto.NotificationDto;
import com.minswap.hrms.security.UserPrincipal;
import com.minswap.hrms.security.oauth2.CurrentUser;
import com.minswap.hrms.service.device.DeviceService;
import com.minswap.hrms.service.notification.NotificationService;
import com.minswap.hrms.service.person.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Validated
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    PersonService personService;

    @GetMapping("push-notifications/{email}")
    public Flux<ServerSentEvent<NotificationResponse>> streamLastMessage(@PathVariable String email) {
//        Long userID = Long.valueOf(2);
        Long userID = personService.getPersonInforByEmail(email.trim()).getPersonId();
        return notificationService.getNotificationsByUserToID(userID);
    }

    @PutMapping("/read-notifications/{notifID}")
    public ResponseEntity<BaseResponse<HttpStatus, Void>> changeNotifStatusToRead(@PathVariable Long notifID) {
        notificationService.changeNotifStatusToRead(notifID);
        return BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
    }

    @GetMapping("employee/notifications")
    public ResponseEntity<BaseResponse<NotificationResponse, Pagination>> getNotificationsByUserID(@RequestParam @Min(1) Integer page,
                                                                                                   @RequestParam @Min(0) Integer limit,
                                                                                                   @CurrentUser UserPrincipal userPrincipal) {
//        Long userID = Long.valueOf(2);
        Long userID = personService.getPersonInforByEmail(userPrincipal.getEmail()).getPersonId();
        return notificationService.getNotificationsByUserID(page, limit, userID);
    }

    @GetMapping("employee/notifications/unread")
    public ResponseEntity<BaseResponse<NotificationResponse, Pagination>> getTotalUnreadNotifications(
            @CurrentUser UserPrincipal userPrincipal) {
//        Long userID = Long.valueOf(2);
        Long userID = personService.getPersonInforByEmail(userPrincipal.getEmail()).getPersonId();
        return notificationService.getTotalUnreadNotifs(userID);
    }

}
