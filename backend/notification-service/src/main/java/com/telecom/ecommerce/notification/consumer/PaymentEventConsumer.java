package com.telecom.ecommerce.notification.consumer;

import com.telecom.ecommerce.notification.dto.NotificationRequest;
import com.telecom.ecommerce.notification.model.Notification.NotificationType;
import com.telecom.ecommerce.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@Slf4j
public class PaymentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);
    
    private final NotificationService notificationService;
    
    @Autowired
    public PaymentEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "payment-events", groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentEvent(Map<String, Object> paymentEvent) {
        log.info("Received payment event: {}", paymentEvent);
        
        try {
            String eventType = (String) paymentEvent.get("eventType");
            String orderId = (String) paymentEvent.get("orderId");
            String recipientEmail = (String) paymentEvent.get("email");
            BigDecimal amount = new BigDecimal(paymentEvent.get("amount").toString());
            
            if (recipientEmail == null || recipientEmail.isEmpty()) {
                log.error("Cannot send notification: missing email address");
                return;
            }
            
            switch (eventType) {
                case "PAYMENT_COMPLETED":
                    sendPaymentConfirmation(recipientEmail, orderId, amount);
                    break;
                case "PAYMENT_FAILED":
                    sendPaymentFailure(recipientEmail, orderId, amount);
                    break;
                case "PAYMENT_REFUNDED":
                    sendRefundConfirmation(recipientEmail, orderId, amount);
                    break;
                default:
                    log.warn("Unknown payment event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Error processing payment event", e);
        }
    }
    
    private void sendPaymentConfirmation(String email, String orderId, BigDecimal amount) {
        NotificationRequest request = new NotificationRequest();
        request.setRecipientEmail(email);
        request.setSubject("Payment Confirmation - Order #" + orderId);
        request.setContent(String.format(
            "<p>Dear Customer,</p>" +
            "<p>Thank you for your payment of $%s for order #%s.</p>" +
            "<p>Your payment has been successfully processed.</p>" +
            "<p>Thanks for shopping with us!</p>",
            amount.toString(), orderId));
        request.setType(NotificationType.PAYMENT_CONFIRMATION);
        
        notificationService.sendNotification(request);
    }
    
    private void sendPaymentFailure(String email, String orderId, BigDecimal amount) {
        NotificationRequest request = new NotificationRequest();
        request.setRecipientEmail(email);
        request.setSubject("Payment Failed - Order #" + orderId);
        request.setContent(String.format(
            "<p>Dear Customer,</p>" +
            "<p>We regret to inform you that your payment of $%s for order #%s could not be processed.</p>" +
            "<p>Please check your payment details and try again, or contact our customer support for assistance.</p>",
            amount.toString(), orderId));
        request.setType(NotificationType.GENERAL);
        
        notificationService.sendNotification(request);
    }
    
    private void sendRefundConfirmation(String email, String orderId, BigDecimal amount) {
        NotificationRequest request = new NotificationRequest();
        request.setRecipientEmail(email);
        request.setSubject("Refund Confirmation - Order #" + orderId);
        request.setContent(String.format(
            "<p>Dear Customer,</p>" +
            "<p>We have processed a refund of $%s for your order #%s.</p>" +
            "<p>The amount should be reflected in your account within 5-7 business days, depending on your payment provider.</p>" +
            "<p>If you have any questions, please contact our customer support.</p>",
            amount.toString(), orderId));
        request.setType(NotificationType.GENERAL);
        
        notificationService.sendNotification(request);
    }
} 