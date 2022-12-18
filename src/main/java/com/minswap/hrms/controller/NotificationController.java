package com.minswap.hrms.controller;

import com.minswap.hrms.entities.Notification;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.NotificationRepository;
import com.minswap.hrms.response.NotificationResponse;
import com.minswap.hrms.security.UserPrincipal;
import com.minswap.hrms.security.oauth2.CurrentUser;
import com.minswap.hrms.service.notification.NotificationService;
import com.minswap.hrms.service.person.PersonService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@Validated
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    private final PersonService personService;
//    private final SimpMessageSendingOperations simpMessagingTemplate;

//    @GetMapping("push-notifications/{email}")
//    public Flux<ServerSentEvent<NotificationResponse>> streamLastMessage(@PathVariable String email) {
////        Long userID = Long.valueOf(2);
//        Long userID = personService.getPersonInforByEmail(email.trim()).getPersonId();
//        return notificationService.getNotificationsByUserToID(userID);
//    }

    @MessageMapping("/read-notifications")
    public void changeNotifStatusToRead(@CurrentUser UserPrincipal user, @Payload Long notifID) {
        notificationService.changeNotifStatusToRead(user, notifID);
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

    @PostMapping("/push-notifications/sample")
    public void send(@Autowired NotificationRepository notificationRepository) throws Exception {
        Notification[] notif = notificationRepository.findByUserTo(1L).toArray(new Notification[0]);
        notificationService.send(notif);
    }

    @PostMapping("/push-notifications/all")
    public void sendAll(@Autowired NotificationRepository notificationRepository) throws Exception {
        Notification[] notif = notificationRepository.findByUserToIsNull().toArray(new Notification[0]);
        notificationService.send(notif);
    }

}
