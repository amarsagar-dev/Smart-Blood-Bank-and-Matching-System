package com.bloodbank.bloodbank.repository;

import com.bloodbank.bloodbank.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserUserIdOrderBySentAtDesc(Integer userId);
    List<Notification> findByUserUserIdAndIsRead(Integer userId, Boolean isRead);
    java.util.Optional<Notification> findByNotificationIdAndUserUserId(Integer notificationId, Integer userId);
}