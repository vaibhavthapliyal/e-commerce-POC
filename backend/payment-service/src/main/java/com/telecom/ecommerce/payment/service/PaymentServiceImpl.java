package com.telecom.ecommerce.payment.service;

import com.telecom.ecommerce.payment.dto.PaymentRequest;
import com.telecom.ecommerce.payment.dto.PaymentResponse;
import com.telecom.ecommerce.payment.model.Payment;
import com.telecom.ecommerce.payment.model.Payment.PaymentStatus;
import com.telecom.ecommerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher eventPublisher;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    
    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, PaymentEventPublisher eventPublisher) {
        this.paymentRepository = paymentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        // Check if payment already exists for this order
        if (paymentRepository.existsByOrderId(paymentRequest.getOrderId())) {
            throw new IllegalStateException("Payment already exists for order: " + paymentRequest.getOrderId());
        }
        
        // Manual builder implementation
        Payment payment = new Payment();
        payment.setOrderId(paymentRequest.getOrderId());
        payment.setAmount(paymentRequest.getAmount());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        
        try {
            // Process the payment (in a real app, this would interact with a payment provider)
            String transactionId = processPaymentWithProvider(paymentRequest);
            payment.setTransactionId(transactionId);
            
            // Set the payment as completed and record the time
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaymentDate(LocalDateTime.now());
            
            Payment savedPayment = paymentRepository.save(payment);
            log.info("Payment processed successfully for order: {}", payment.getOrderId());
            
            // Publish payment completed event
            String customerEmail = paymentRequest.getCardHolderName() != null 
                ? paymentRequest.getCardHolderName() + "@example.com" // Just for simulation
                : "customer@example.com";
            eventPublisher.publishPaymentCompleted(savedPayment, customerEmail);
            
            return mapToPaymentResponse(savedPayment);
        } catch (Exception e) {
            log.error("Payment processing failed for order: {}", payment.getOrderId(), e);
            
            // Set payment as failed
            payment.setStatus(PaymentStatus.FAILED);
            Payment savedPayment = paymentRepository.save(payment);
            
            // Publish payment failed event
            String customerEmail = paymentRequest.getCardHolderName() != null 
                ? paymentRequest.getCardHolderName() + "@example.com" // Just for simulation
                : "customer@example.com";
            eventPublisher.publishPaymentFailed(savedPayment, customerEmail);
            
            throw new IllegalStateException("Payment processing failed: " + e.getMessage());
        }
    }

    @Override
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));
        
        return mapToPaymentResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found for order: " + orderId));
        
        return mapToPaymentResponse(payment);
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentResponse refundPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));
        
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Only completed payments can be refunded");
        }
        
        // Process refund with payment provider (in a real app)
        processRefundWithProvider(payment);
        
        payment.setStatus(PaymentStatus.REFUNDED);
        Payment updatedPayment = paymentRepository.save(payment);
        
        log.info("Payment refunded for order: {}", payment.getOrderId());
        
        // Publish payment refunded event
        eventPublisher.publishPaymentRefunded(updatedPayment, "customer@example.com");
        
        return mapToPaymentResponse(updatedPayment);
    }

    @Override
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new EntityNotFoundException("Payment not found with id: " + id);
        }
        
        paymentRepository.deleteById(id);
        log.info("Deleted payment with id: {}", id);
    }
    
    // Simulate payment processing with a provider
    private String processPaymentWithProvider(PaymentRequest request) {
        // In a real app, this would call an external payment provider API
        try {
            // Simulate processing time
            Thread.sleep(1000);
            
            // Randomly fail about 10% of payments for demonstration purposes
            if (Math.random() < 0.1) {
                throw new RuntimeException("Payment declined by provider");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Return a mock transaction ID
        return UUID.randomUUID().toString();
    }
    
    // Simulate refund processing with a provider
    private void processRefundWithProvider(Payment payment) {
        // In a real app, this would call an external payment provider API
        try {
            // Simulate processing time
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private PaymentResponse mapToPaymentResponse(Payment payment) {
        String paymentDateStr = payment.getPaymentDate() != null
                ? payment.getPaymentDate().format(FORMATTER)
                : null;
        
        // Manual builder implementation
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setOrderId(payment.getOrderId());
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setTransactionId(payment.getTransactionId());
        response.setPaymentDate(paymentDateStr);
        return response;
    }
} 