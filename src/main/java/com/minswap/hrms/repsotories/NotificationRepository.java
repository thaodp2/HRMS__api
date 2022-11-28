package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserToAndAndDelivered(Long userTo, Integer delivered);

    List<Notification> findByUserTo(Long userTo);
}
