package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.entities.Notification;
import com.minswap.hrms.repsotories.NotificationRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder("id")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDto {

    @JsonProperty("id")
    private Long notificationId;

    private String userTo;

    private String userFrom;

    private String content;

    private String redirectUrl;

    private Integer delivered;

    private Integer isRead;

    private String avtUrl;

    @JsonFormat(pattern = CommonConstant.YYYY_MM_DD_HH_MM_SS, shape = JsonFormat.Shape.STRING)
    private Date createDate;
}
