package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.minswap.hrms.constants.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder("id")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDto {

    public NotificationDto(Long notificationId, String userTo, String userFrom, String content, String redirectUrl, Integer delivered, Integer isRead, String avtUrl, Date createDate) {
        this.notificationId = notificationId;
        this.userTo = userTo;
        this.userFrom = userFrom;
        this.content = content;
        this.redirectUrl = redirectUrl;
        this.delivered = delivered;
        this.isRead = isRead;
        this.avtUrl = avtUrl;
        this.createDate = createDate;
    }

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
