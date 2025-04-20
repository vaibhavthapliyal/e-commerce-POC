package com.telecom.ecommerce.cart.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private String type;
    private String imageUrl;
    private String dataAllowance;
    private String brand;
    private Integer quantity;
    
    // Discount info
    private Integer discountPercentage;
    private String discountExpiry;
    
    // Manual getter methods
    public Long getProductId() {
        return productId;
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
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public Integer getDiscountPercentage() {
        return discountPercentage;
    }
    
    public String getDiscountExpiry() {
        return discountExpiry;
    }
    
    // Manual setter methods
    public void setProductId(Long productId) {
        this.productId = productId;
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
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public void setDiscountPercentage(Integer discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
    
    public void setDiscountExpiry(String discountExpiry) {
        this.discountExpiry = discountExpiry;
    }
} 