package com.telecom.ecommerce.payment.service;

import com.telecom.ecommerce.payment.dto.PaymentRequest;
import com.telecom.ecommerce.payment.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse processPayment(PaymentRequest paymentRequest);
    
    PaymentResponse getPaymentById(Long id);
    
    PaymentResponse getPaymentByOrderId(String orderId);
    
    List<PaymentResponse> getAllPayments();
    
    PaymentResponse refundPayment(Long id);
    
    void deletePayment(Long id);
} 