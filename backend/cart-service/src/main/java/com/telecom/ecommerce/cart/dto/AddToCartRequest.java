package com.telecom.ecommerce.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    // Manual getter methods
    public Long getProductId() {
        return productId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    // Manual setter methods
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
} 