package com.telecom.ecommerce.notification.dto;

import com.telecom.ecommerce.notification.model.Notification.NotificationStatus;
import com.telecom.ecommerce.notification.model.Notification.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String recipientEmail;
    private String subject;
    private NotificationType type;
    private NotificationStatus status;
    private String sentAt;
    
    // Manual getter methods
    public Long getId() {
        return id;
    }
    
    public String getRecipientEmail() {
        return recipientEmail;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public NotificationStatus getStatus() {
        return status;
    }
    
    public String getSentAt() {
        return sentAt;
    }
    
    // Manual setter methods
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public void setStatus(NotificationStatus status) {
        this.status = status;
    }
    
    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }
} 