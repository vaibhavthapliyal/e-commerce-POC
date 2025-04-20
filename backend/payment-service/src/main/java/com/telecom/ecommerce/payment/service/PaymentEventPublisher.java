package com.telecom.ecommerce.payment.service;

import com.telecom.ecommerce.payment.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PaymentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "payment-events";
    
    @Autowired
    public PaymentEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishPaymentCompleted(Payment payment, String email) {
        Map<String, Object> event = createPaymentEvent(payment, "PAYMENT_COMPLETED", email);
        sendEvent(event);
        log.info("Published payment completed event for order: {}", payment.getOrderId());
    }
    
    public void publishPaymentFailed(Payment payment, String email) {
        Map<String, Object> event = createPaymentEvent(payment, "PAYMENT_FAILED", email);
        sendEvent(event);
        log.info("Published payment failed event for order: {}", payment.getOrderId());
    }
    
    public void publishPaymentRefunded(Payment payment, String email) {
        Map<String, Object> event = createPaymentEvent(payment, "PAYMENT_REFUNDED", email);
        sendEvent(event);
        log.info("Published payment refunded event for order: {}", payment.getOrderId());
    }
    
    private Map<String, Object> createPaymentEvent(Payment payment, String eventType, String email) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", eventType);
        event.put("paymentId", payment.getId());
        event.put("orderId", payment.getOrderId());
        event.put("amount", payment.getAmount());
        event.put("status", payment.getStatus().name());
        event.put("timestamp", System.currentTimeMillis());
        event.put("email", email);
        return event;
    }
    
    private void sendEvent(Map<String, Object> event) {
        try {
            kafkaTemplate.send(TOPIC, event);
        } catch (Exception e) {
            log.error("Error publishing payment event to Kafka", e);
        }
    }
} 