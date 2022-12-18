package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Notification;
import com.minswap.hrms.response.dto.NotificationDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserToAndDelivered(Long userTo, Integer delivered);

    List<Notification> findByUserTo(Long userTo);
    List<Notification> findByUserToIsNull();

    List<Notification> findByUserToAndIsRead(Long userTo,Integer isRead);

    @Query("SELECT new com.minswap.hrms.response.dto.NotificationDto(" +
            "n.notificationId as notificationId,p.fullName as userTo," +
            "case " +
            "when p2.fullName is null then 'MinSwap Company' " +
            "ELSE p2.fullName " +
            "end as userFrom, n.content as content, n.redirectUrl as redirectUrl, n.delivered as delivered, n.isRead as isRead," +
            "case " +
            "when p2.fullName is null then 'https://firebasestorage.googleapis.com/v0/b/hrms-3e889.appspot.com/o/images%2Favatar%2Fcompany-logo_jkahsdhhashdghajs.png?alt=media&token=fe3e9776-c8f9-4ba8-baab-71231ca75754' " +
            "ELSE p2.avatarImg " +
            "end as avtUrl, n.createDate) " +
            "from Notification n " +
            "left join Person p on n.userTo = p.personId " +
            "LEFT join Person p2 on n.userFrom = p2.personId " +
            "where p.personId = :personId " +
            "order by n.createDate desc")
    Page<NotificationDto> getMyNotifications(@Param("personId") Long personId,
                                               Pageable pageable);

    @Query("SELECT new com.minswap.hrms.response.dto.NotificationDto(" +
            "n.notificationId as notificationId,p.fullName as userTo," +
            "case " +
            "when p2.fullName is null then 'MinSwap Company' " +
            "ELSE p2.fullName " +
            "end as userFrom, n.content as content, n.redirectUrl as redirectUrl, n.delivered as delivered, n.isRead as isRead," +
            "case " +
            "when p2.fullName is null then 'https://firebasestorage.googleapis.com/v0/b/hrms-3e889.appspot.com/o/images%2Favatar%2Fcompany-logo_jkahsdhhashdghajs.png?alt=media&token=fe3e9776-c8f9-4ba8-baab-71231ca75754' " +
            "ELSE p2.avatarImg " +
            "end as avtUrl, n.createDate) " +
            "from Notification n " +
            "left join Person p on n.userTo = p.personId " +
            "LEFT join Person p2 on n.userFrom = p2.personId " +
            "where p.personId = :personId and n.delivered = 0 " +
            "order by n.createDate desc")
    List<NotificationDto> getNotiByUserToAndDelivered(@Param("personId") Long personId);
}
