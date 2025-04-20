package com.telecom.ecommerce.notification.service;

import com.telecom.ecommerce.notification.dto.NotificationRequest;
import com.telecom.ecommerce.notification.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {

    NotificationResponse sendNotification(NotificationRequest notificationRequest);
    
    NotificationResponse getNotificationById(Long id);
    
    List<NotificationResponse> getAllNotifications();
    
    List<NotificationResponse> getNotificationsByEmail(String email);
    
    void resendFailedNotifications();
    
    void deleteNotification(Long id);
} 