package com.telecom.ecommerce.notification.repository;

import com.telecom.ecommerce.notification.model.Notification;
import com.telecom.ecommerce.notification.model.Notification.NotificationStatus;
import com.telecom.ecommerce.notification.model.Notification.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByStatus(NotificationStatus status);
    
    List<Notification> findByRecipientEmail(String recipientEmail);
    
    List<Notification> findByType(NotificationType type);
    
    List<Notification> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<Notification> findByStatusAndType(NotificationStatus status, NotificationType type);
} 