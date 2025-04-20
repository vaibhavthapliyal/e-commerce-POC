package com.telecom.ecommerce.product.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Column(length = 1000)
    private String description;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotBlank
    @Column(name = "type")
    private String type; // 'tariff' or 'device'

    private String imageUrl;

    @Column(name = "data_allowance")
    private String dataAllowance; // For tariffs: '1GB', '5GB', '10GB', 'Unlimited', etc.

    private String brand; // For devices: 'Apple', 'Samsung', etc.

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }
    
    // Manual getter methods in case Lombok isn't working
    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public String getType() {
        return type;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public String getDataAllowance() {
        return dataAllowance;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Manual setter methods in case Lombok isn't working
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public void setDataAllowance(String dataAllowance) {
        this.dataAllowance = dataAllowance;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 