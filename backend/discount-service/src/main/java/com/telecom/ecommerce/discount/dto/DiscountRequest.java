package com.telecom.ecommerce.discount.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountRequest {
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @NotNull(message = "Percentage is required")
    @Min(value = 1, message = "Discount percentage must be at least 1")
    @Max(value = 100, message = "Discount percentage must not exceed 100")
    private Integer percentage;
    
    @NotBlank(message = "Expiry time is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?(?:Z|[+-]\\d{2}:\\d{2})?", 
             message = "Invalid ISO date time format (yyyy-MM-ddTHH:mm:ss)")
    private String expiryTime;
    
    // Manual getter methods
    public Long getProductId() {
        return productId;
    }
    
    public Integer getPercentage() {
        return percentage;
    }
    
    public String getExpiryTime() {
        return expiryTime;
    }
    
    // Manual setter methods
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }
    
    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }
} 