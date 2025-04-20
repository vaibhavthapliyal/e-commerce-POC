package com.telecom.ecommerce.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "recipient_email")
    @Email
    private String recipientEmail;

    @NotBlank
    private String subject;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationType type;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = NotificationStatus.PENDING;
    }

    public enum NotificationType {
        ORDER_CONFIRMATION,
        PAYMENT_CONFIRMATION,
        SHIPPING_UPDATE,
        PASSWORD_RESET,
        ACCOUNT_ACTIVATION,
        GENERAL
    }

    public enum NotificationStatus {
        PENDING,
        SENT,
        FAILED
    }
    
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
    
    public String getContent() {
        return content;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public NotificationStatus getStatus() {
        return status;
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
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setStatus(NotificationStatus status) {
        this.status = status;
    }
} 