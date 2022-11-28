package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.entities.Notification;
import lombok.Data;

import java.util.List;

@Data
public class NotificationResponse {
    @JsonProperty(value = "items")
    private List<Notification> notificationList;

    public NotificationResponse(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }
}
