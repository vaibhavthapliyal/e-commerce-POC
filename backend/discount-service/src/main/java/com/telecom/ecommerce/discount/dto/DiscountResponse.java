package com.telecom.ecommerce.discount.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountResponse {
    private Long id;
    private Long productId;
    private Integer percentage;
    private String expiryTime;
    private boolean active;
    
    // Manual getter methods
    public Long getId() {
        return id;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public Integer getPercentage() {
        return percentage;
    }
    
    public String getExpiryTime() {
        return expiryTime;
    }
    
    public boolean isActive() {
        return active;
    }
    
    // Manual setter methods
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }
    
    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
} 