package com.minswap.hrms.service.notification;

import com.minswap.hrms.entities.Notification;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.NotificationRepository;
import com.minswap.hrms.response.NotificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    @Override
    public ResponseEntity<BaseResponse<NotificationResponse, Pagination>> getNotificationsByUserID(Long userID) {
        List<Notification> list = notificationRepository.findByUserTo(userID);
        NotificationResponse response = new NotificationResponse(list);
        ResponseEntity<BaseResponse<NotificationResponse, Pagination>> responseEntity
                = BaseResponse.ofSucceededOffset(response, null);
        return responseEntity;
    }

    @Override
    public List<Notification> getNotifs(Long userID) {
        List<Notification> notifs = notificationRepository.findByUserToAndAndDelivered(userID, 0);
        if (!notifs.isEmpty()) {
            notifs.forEach(x -> x.setDelivered(1));
            notificationRepository.saveAll(notifs);
        }
        return notifs;
    }

    @Override
    public Flux<ServerSentEvent<List<Notification>>> getNotificationsByUserToID(Long userID) {
        if (userID != null) {
            return Flux.interval(Duration.ofSeconds(1))
                    .publishOn(Schedulers.boundedElastic())
                    .map(sequence -> ServerSentEvent.<List<Notification>>builder().id(String.valueOf(sequence))
                            .event("user-list-event").data(getNotifs(userID))
                            .build());
        }

        return Flux.interval(Duration.ofSeconds(1)).map(sequence -> ServerSentEvent.<List<Notification>>builder()
                .id(String.valueOf(sequence)).event("user-list-event").data(new ArrayList<>()).build());
    }

    @Override
    public void changeNotifStatusToRead(Long notifID) {
        Notification notification = notificationRepository.findById(notifID).orElse(null);
        if (notification != null) {
            notification.setIsRead(1);
            notificationRepository.save(notification);
        }
    }
}
