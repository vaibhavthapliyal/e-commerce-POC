package com.telecom.ecommerce.discount.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "discounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "product_id")
    private Long productId;

    @NotNull
    @Min(1)
    @Max(100)
    private Integer percentage;

    @NotNull
    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    private boolean active;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        active = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }
    
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
    
    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
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
    
    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
} 