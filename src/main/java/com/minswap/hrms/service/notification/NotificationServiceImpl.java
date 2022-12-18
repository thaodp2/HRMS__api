package com.minswap.hrms.service.notification;

import com.minswap.hrms.entities.Notification;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.NotificationRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.response.NotificationResponse;
import com.minswap.hrms.response.dto.NotificationDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final SimpMessagingTemplate template;
    private final NotificationRepository notificationRepository;
    private final PersonRepository personRepository;

    @Override
    public ResponseEntity<BaseResponse<NotificationResponse, Pagination>> getNotificationsByUserID(Integer page, Integer limit, Long userID) {
        //List<Notification> list = notificationRepository.findByUserTo(userID);
        ResponseEntity<BaseResponse<NotificationResponse, Pagination>> responseEntity = null;
        Page<NotificationDto> pageInfo = notificationRepository.getMyNotifications(userID, PageRequest.of(page - 1, limit));
        if(pageInfo!=null) {
            Pagination pagination = new Pagination(page, limit);
            pagination.setTotalRecords(pageInfo);

            NotificationResponse response = new NotificationResponse(pageInfo.getContent());
            responseEntity
                    = BaseResponse.ofSucceededOffset(response, pagination);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Long, Pagination>> getTotalUnreadNotifs(Long userID) {
        Long total = getTotal(userID);
        return BaseResponse.ofSucceededOffset(total, null);
    }

    public Long getTotal(Long userID) {
        List<Notification> notifs = notificationRepository.findByUserToAndIsRead(userID,0);
        if (notifs != null && !notifs.isEmpty()) {
            return (long) notifs.size();
        }
        return 0L;
    }

    @Override
    public void changeNotifStatusToRead(Long notifID) {
        Notification notification = notificationRepository.findById(notifID).orElse(null);
        log.debug("##DEBUG## Notification: {}", notification);
        if (notification != null) {
            notification.setIsRead(1);
            notificationRepository.save(notification);
        }
    }

    @Override
    public void send(Notification... notifs) {
        Arrays.stream(notifs).forEach(notif -> {
                    template.convertAndSend("/topic/notification/" + notif.getUserTo(), List.of(toDto(notif)));
        });
    }

    private NotificationDto toDto(Notification notification) {
        return new NotificationDto(
                notification.getNotificationId(),
                personRepository.findPersonByPersonId(notification.getUserTo()).orElse(new Person()).getFullName(),
                personRepository.findPersonByPersonId(notification.getUserFrom()).orElse(new Person()).getFullName(),
                notification.getContent(),
                notification.getRedirectUrl(),
                notification.getDelivered(),
                notification.getIsRead(),
                personRepository.findPersonByPersonId(notification.getUserFrom()).orElse(new Person()).getAvatarImg(),
                notification.getCreateDate()
        );
    }
}
