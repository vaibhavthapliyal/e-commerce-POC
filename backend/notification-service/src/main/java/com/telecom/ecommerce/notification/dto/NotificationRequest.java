package com.telecom.ecommerce.notification.dto;

import com.telecom.ecommerce.notification.model.Notification.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    
    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    private String recipientEmail;
    
    @NotBlank(message = "Subject is required")
    private String subject;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    @NotNull(message = "Notification type is required")
    private NotificationType type;
    
    // Manual getter methods
    public String getRecipientEmail() {
        return recipientEmail;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public String getContent() {
        return content;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    // Manual setter methods
    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
} 