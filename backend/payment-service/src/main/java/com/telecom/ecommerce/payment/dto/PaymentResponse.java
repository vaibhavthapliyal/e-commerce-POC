package com.telecom.ecommerce.payment.dto;

import com.telecom.ecommerce.payment.model.Payment.PaymentMethod;
import com.telecom.ecommerce.payment.model.Payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private String orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private String transactionId;
    private String paymentDate;
    
    // Manual getter methods
    public Long getId() {
        return id;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public PaymentStatus getStatus() {
        return status;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public String getPaymentDate() {
        return paymentDate;
    }
    
    // Manual setter methods
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
    
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }
} 