package com.telecom.ecommerce.cart.service;

import com.telecom.ecommerce.cart.dto.CartResponse;
import com.telecom.ecommerce.cart.model.Cart;

public interface CartService {

    Cart getCart(String cartId);
    
    CartResponse getCartResponse(String cartId);
    
    CartResponse addToCart(String cartId, Long productId, Integer quantity);
    
    CartResponse updateCartItem(String cartId, Long productId, Integer quantity);
    
    CartResponse removeFromCart(String cartId, Long productId);
    
    void deleteCart(String cartId);
} 