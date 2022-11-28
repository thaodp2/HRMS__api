package com.minswap.hrms.entities;

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

    public Notification(String content,Integer delivered,String notificationType,Integer isRead, Long userFrom,Long userTo) {
        this.userTo = userTo;
        this.userFrom = userFrom;
        this.content = content;
        this.notificationType = notificationType;
        this.delivered = delivered;
        this.isRead = isRead;
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

    @Column(name = "notification_type")
    private String notificationType;

    @Column(name = "delivered")
    private Integer delivered;

    @Column(name = "is_read")
    private Integer isRead;
}
