package com.telecom.ecommerce.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String type;
    private String imageUrl;
    private String dataAllowance;
    private String brand;
    
    // Manual getter methods
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
    
    // Manual setter methods
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
} 