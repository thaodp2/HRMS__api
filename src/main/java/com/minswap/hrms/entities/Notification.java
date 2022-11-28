package com.minswap.hrms.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.minswap.hrms.constants.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = Notification.TABLE_NAME)
public class Notification {
    public static final String TABLE_NAME = "notification";

    public Notification(String content,Integer delivered,String redirectUrl,Integer isRead, Long userFrom,Long userTo, Date createDate) {
        this.userTo = userTo;
        this.userFrom = userFrom;
        this.content = content;
        this.redirectUrl = redirectUrl;
        this.delivered = delivered;
        this.isRead = isRead;
        this.createDate = createDate;
    }

    @Id
    @Column(name = "notification_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(name = "user_to")
    private Long userTo;

    @Column(name = "user_from")
    private Long userFrom;

    @Column(name = "content")
    private String content;

    @Column(name = "redirect_url")
    private String redirectUrl;

    @Column(name = "delivered")
    private Integer delivered;

    @Column(name = "is_read")
    private Integer isRead;

    @JsonFormat(pattern = CommonConstant.YYYY_MM_DD_HH_MM_SS, shape = JsonFormat.Shape.STRING)
    @Column(name = "create_date")
    private Date createDate;
}
