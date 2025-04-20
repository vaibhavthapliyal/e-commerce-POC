package com.telecom.ecommerce.payment.dto;

import com.telecom.ecommerce.payment.model.Payment.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    
    @NotBlank(message = "Order ID is required")
    private String orderId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
    
    // Credit card details - only required for credit/debit card payments
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvv;
    
    // Manual getter methods
    public String getOrderId() {
        return orderId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public String getCardNumber() {
        return cardNumber;
    }
    
    public String getCardHolderName() {
        return cardHolderName;
    }
    
    public String getExpiryDate() {
        return expiryDate;
    }
    
    public String getCvv() {
        return cvv;
    }
    
    // Manual setter methods
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    
    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }
    
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
} 