package com.telecom.ecommerce.payment.repository;

import com.telecom.ecommerce.payment.model.Payment;
import com.telecom.ecommerce.payment.model.Payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByOrderId(String orderId);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    List<Payment> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);
    
    boolean existsByOrderId(String orderId);
} 