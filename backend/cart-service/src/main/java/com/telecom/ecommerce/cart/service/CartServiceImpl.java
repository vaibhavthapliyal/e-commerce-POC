package com.telecom.ecommerce.cart.service;

import com.telecom.ecommerce.cart.client.ProductServiceClient;
import com.telecom.ecommerce.cart.dto.CartResponse;
import com.telecom.ecommerce.cart.dto.ProductDto;
import com.telecom.ecommerce.cart.model.Cart;
import com.telecom.ecommerce.cart.model.CartItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductServiceClient productServiceClient;
    
    private static final String CART_KEY_PREFIX = "cart:";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    
    @Value("${cart.expiration.hours}")
    private int cartExpirationHours;
    
    @Autowired
    public CartServiceImpl(RedisTemplate<String, Object> redisTemplate, ProductServiceClient productServiceClient) {
        this.redisTemplate = redisTemplate;
        this.productServiceClient = productServiceClient;
    }

    @Override
    public Cart getCart(String cartId) {
        String key = CART_KEY_PREFIX + cartId;
        Cart cart = (Cart) redisTemplate.opsForValue().get(key);
        
        if (cart == null) {
            cart = createNewCart(cartId);
            redisTemplate.opsForValue().set(key, cart);
            redisTemplate.expire(key, cartExpirationHours, TimeUnit.HOURS);
        }
        
        return cart;
    }

    @Override
    public CartResponse getCartResponse(String cartId) {
        Cart cart = getCart(cartId);
        return mapToCartResponse(cart);
    }

    @Override
    public CartResponse addToCart(String cartId, Long productId, Integer quantity) {
        Cart cart = getCart(cartId);
        
        // Get product details from product service
        ProductDto product = productServiceClient.getProductById(productId);
        
        // Check if the product already exists in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
        
        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            // Add new item to cart
            CartItem newItem = mapToCartItem(product, quantity);
            cart.getItems().add(newItem);
        }
        
        // Update cart total
        updateCartTotal(cart);
        
        // Update cart in Redis
        saveCart(cart);
        
        return mapToCartResponse(cart);
    }

    @Override
    public CartResponse updateCartItem(String cartId, Long productId, Integer quantity) {
        Cart cart = getCart(cartId);
        
        if (quantity <= 0) {
            return removeFromCart(cartId, productId);
        }
        
        // Find and update the item
        cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));
        
        // Update cart total
        updateCartTotal(cart);
        
        // Update cart in Redis
        saveCart(cart);
        
        return mapToCartResponse(cart);
    }

    @Override
    public CartResponse removeFromCart(String cartId, Long productId) {
        Cart cart = getCart(cartId);
        
        // Remove the item
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        
        // Update cart total
        updateCartTotal(cart);
        
        // Update cart in Redis
        saveCart(cart);
        
        return mapToCartResponse(cart);
    }

    @Override
    public void deleteCart(String cartId) {
        String key = CART_KEY_PREFIX + cartId;
        redisTemplate.delete(key);
    }
    
    private Cart createNewCart(String cartId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusHours(cartExpirationHours);
        
        // Manual builder implementation
        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setTotal(BigDecimal.ZERO);
        cart.setCreatedAt(now);
        cart.setUpdatedAt(now);
        cart.setExpiresAt(expiry);
        return cart;
    }
    
    private void updateCartTotal(Cart cart) {
        BigDecimal total = cart.getItems().stream()
                .map(item -> {
                    BigDecimal price = item.getPrice();
                    // Apply discount if available
                    if (item.getDiscountPercentage() != null && item.getDiscountPercentage() > 0) {
                        BigDecimal discount = price.multiply(BigDecimal.valueOf(item.getDiscountPercentage() / 100.0));
                        price = price.subtract(discount);
                    }
                    return price.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        cart.setTotal(total);
        cart.setUpdatedAt(LocalDateTime.now());
    }
    
    private void saveCart(Cart cart) {
        String key = CART_KEY_PREFIX + cart.getId();
        redisTemplate.opsForValue().set(key, cart);
        redisTemplate.expire(key, cartExpirationHours, TimeUnit.HOURS);
    }
    
    private CartItem mapToCartItem(ProductDto product, Integer quantity) {
        // Manual builder implementation
        CartItem item = new CartItem();
        item.setProductId(product.getId());
        item.setName(product.getName());
        item.setDescription(product.getDescription());
        item.setPrice(product.getPrice());
        item.setType(product.getType());
        item.setImageUrl(product.getImageUrl());
        item.setDataAllowance(product.getDataAllowance());
        item.setBrand(product.getBrand());
        item.setQuantity(quantity);
        return item;
    }
    
    private CartResponse mapToCartResponse(Cart cart) {
        // Manual builder implementation
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setItems(cart.getItems());
        response.setTotal(cart.getTotal());
        response.setExpiresAt(cart.getExpiresAt().format(FORMATTER));
        return response;
    }
    
    // Utility method to generate a cart ID
    public static String generateCartId() {
        return UUID.randomUUID().toString();
    }
} 