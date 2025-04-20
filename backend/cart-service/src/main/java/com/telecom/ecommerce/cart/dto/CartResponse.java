package com.telecom.ecommerce.cart.dto;

import com.telecom.ecommerce.cart.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private String cartId;
    private List<CartItem> items;
    private BigDecimal total;
    private String expiresAt;
    
    // Manual getter methods
    public String getCartId() {
        return cartId;
    }
    
    public List<CartItem> getItems() {
        return items;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public String getExpiresAt() {
        return expiresAt;
    }
    
    // Manual setter methods
    public void setCartId(String cartId) {
        this.cartId = cartId;
    }
    
    public void setItems(List<CartItem> items) {
        this.items = items;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }
} 