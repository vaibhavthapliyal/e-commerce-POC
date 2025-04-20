package com.telecom.ecommerce.notification.service;

import com.telecom.ecommerce.notification.dto.NotificationRequest;
import com.telecom.ecommerce.notification.dto.NotificationResponse;
import com.telecom.ecommerce.notification.model.Notification;
import com.telecom.ecommerce.notification.model.Notification.NotificationStatus;
import com.telecom.ecommerce.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository, JavaMailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
    }

    @Override
    public NotificationResponse sendNotification(NotificationRequest request) {
        // Manual builder implementation
        Notification notification = new Notification();
        notification.setRecipientEmail(request.getRecipientEmail());
        notification.setSubject(request.getSubject());
        notification.setContent(request.getContent());
        notification.setType(request.getType());
        
        Notification savedNotification = notificationRepository.save(notification);
        
        // Send email asynchronously
        sendEmailAsync(savedNotification);
        
        return mapToNotificationResponse(savedNotification);
    }

    @Override
    public NotificationResponse getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + id));
        
        return mapToNotificationResponse(notification);
    }

    @Override
    public List<NotificationResponse> getAllNotifications() {
        List<Notification> notifications = notificationRepository.findAll();
        return notifications.stream()
                .map(this::mapToNotificationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getNotificationsByEmail(String email) {
        List<Notification> notifications = notificationRepository.findByRecipientEmail(email);
        return notifications.stream()
                .map(this::mapToNotificationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void resendFailedNotifications() {
        List<Notification> failedNotifications = notificationRepository.findByStatus(NotificationStatus.FAILED);
        
        if (!failedNotifications.isEmpty()) {
            failedNotifications.forEach(this::sendEmailAsync);
            log.info("Attempted to resend {} failed notifications", failedNotifications.size());
        }
    }

    @Override
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new EntityNotFoundException("Notification not found with id: " + id);
        }
        
        notificationRepository.deleteById(id);
        log.info("Deleted notification with id: {}", id);
    }
    
    @Async
    protected void sendEmailAsync(Notification notification) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setTo(notification.getRecipientEmail());
            helper.setSubject(notification.getSubject());
            helper.setText(notification.getContent(), true); // true for HTML content
            
            mailSender.send(message);
            
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            
            log.info("Email sent successfully to: {}", notification.getRecipientEmail());
        } catch (MessagingException e) {
            notification.setStatus(NotificationStatus.FAILED);
            log.error("Failed to send email to: {}", notification.getRecipientEmail(), e);
        }
        
        notificationRepository.save(notification);
    }
    
    private NotificationResponse mapToNotificationResponse(Notification notification) {
        String sentAtStr = notification.getSentAt() != null
                ? notification.getSentAt().format(FORMATTER)
                : null;
        
        // Manual builder implementation
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setRecipientEmail(notification.getRecipientEmail());
        response.setSubject(notification.getSubject());
        response.setType(notification.getType());
        response.setStatus(notification.getStatus());
        response.setSentAt(sentAtStr);
        return response;
    }
} 