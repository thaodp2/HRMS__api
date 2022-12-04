package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.entities.Notification;
import com.minswap.hrms.response.dto.NotificationDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class NotificationResponse {
    @JsonProperty(value = "items")
    private List<NotificationDto> notificationList;

    public NotificationResponse(List<NotificationDto> notificationList) {
        this.notificationList = notificationList;
    }
}
