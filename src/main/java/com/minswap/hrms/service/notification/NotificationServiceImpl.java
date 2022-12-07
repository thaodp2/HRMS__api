package com.minswap.hrms.service.notification;

import com.minswap.hrms.entities.Notification;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.NotificationRepository;
import com.minswap.hrms.response.NotificationResponse;
import com.minswap.hrms.response.dto.NotificationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public ResponseEntity<BaseResponse<NotificationResponse, Pagination>> getNotificationsByUserID(Integer page, Integer limit, Long userID) {
        //List<Notification> list = notificationRepository.findByUserTo(userID);
        Page<NotificationDto> pageInfo = notificationRepository.getMyNotifications(userID, PageRequest.of(page - 1, limit));
        Pagination pagination = new Pagination(page, limit);
        pagination.setTotalRecords(pageInfo);

        NotificationResponse response = new NotificationResponse(pageInfo.getContent());
        ResponseEntity<BaseResponse<NotificationResponse, Pagination>> responseEntity
                = BaseResponse.ofSucceededOffset(response, pagination);
        return responseEntity;
    }

    @Override
    public NotificationResponse getNotifs(Long userID) {
        List<Notification> notifsSave = notificationRepository.findByUserToAndDelivered(userID, 0);
        List<NotificationDto> notifs = notificationRepository.getNotiByUserToAndDelivered(userID);

        Long total = getTotal(userID);
        if ((notifs != null && !notifs.isEmpty())) {
            for (NotificationDto noti : notifs) {
                noti.setTotalNotificationNotRead(total);
            }
        }
        NotificationResponse response = new NotificationResponse(notifs);

        if (notifsSave != null && !notifsSave.isEmpty()) {
            notifsSave.forEach(x -> x.setDelivered(1));
            notificationRepository.saveAll(notifsSave);
        }
        return response;
    }

    @Override
    public ResponseEntity<BaseResponse<NotificationResponse, Pagination>> getTotalUnreadNotifs(Long userID) {
        NotificationDto notificationDto = new NotificationDto();
        Long total = getTotal(userID);
        notificationDto.setTotalNotificationNotRead(total);
        List<NotificationDto> list = new ArrayList<>();
        list.add(notificationDto);
        NotificationResponse response = new NotificationResponse(list);
        ResponseEntity<BaseResponse<NotificationResponse, Pagination>> responseEntity
                = BaseResponse.ofSucceededOffset(response, null);
        return responseEntity;
    }

    public Long getTotal(Long userID) {
        List<Notification> notifs = notificationRepository.findByUserToAndIsRead(userID,0);
        if (notifs != null && !notifs.isEmpty()) {
            return (long) notifs.size();
        }
        return (long) 0;
    }

    public Flux<ServerSentEvent<NotificationResponse>> getNotificationsByUserToID(Long userID) {
        if (userID != null) {
            return Flux.interval(Duration.ofSeconds(1))
                    .publishOn(Schedulers.boundedElastic())
                    .map(sequence -> ServerSentEvent.<NotificationResponse>builder().id(String.valueOf(sequence))
                            .event("user-list-event").data(getNotifs(userID))
                            .build());
        }

        return Flux.interval(Duration.ofSeconds(1)).map(sequence -> ServerSentEvent.<NotificationResponse>builder()
                .id(String.valueOf(sequence)).event("user-list-event").data(new NotificationResponse()).build());
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
