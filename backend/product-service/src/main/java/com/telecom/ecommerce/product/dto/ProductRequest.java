package com.telecom.ecommerce.product.dto;

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
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    @NotBlank(message = "Product type is required")
    private String type;
    
    private String imageUrl;
    
    private String dataAllowance;
    
    private String brand;
    
    // Manual getter methods in case Lombok isn't working
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
} 