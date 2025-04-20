package com.telecom.ecommerce.cart.controller;

import com.telecom.ecommerce.cart.dto.AddToCartRequest;
import com.telecom.ecommerce.cart.dto.CartResponse;
import com.telecom.ecommerce.cart.dto.UpdateCartRequest;
import com.telecom.ecommerce.cart.service.CartService;
import com.telecom.ecommerce.cart.service.CartServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private static final String CART_COOKIE_NAME = "cart_id";
    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 30; // 30 days
    
    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(HttpServletRequest request, HttpServletResponse response) {
        String cartId = getOrCreateCartId(request, response);
        CartResponse cartResponse = cartService.getCartResponse(cartId);
        return ResponseEntity.ok(cartResponse);
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            HttpServletRequest servletRequest,
            HttpServletResponse response
    ) {
        String cartId = getOrCreateCartId(servletRequest, response);
        CartResponse cartResponse = cartService.addToCart(cartId, request.getProductId(), request.getQuantity());
        return new ResponseEntity<>(cartResponse, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<CartResponse> updateCart(
            @Valid @RequestBody UpdateCartRequest request,
            HttpServletRequest servletRequest,
            HttpServletResponse response
    ) {
        String cartId = getOrCreateCartId(servletRequest, response);
        CartResponse cartResponse = cartService.updateCartItem(cartId, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(cartResponse);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<CartResponse> removeFromCart(
            @RequestBody UpdateCartRequest request,
            HttpServletRequest servletRequest,
            HttpServletResponse response
    ) {
        String cartId = getOrCreateCartId(servletRequest, response);
        CartResponse cartResponse = cartService.removeFromCart(cartId, request.getProductId());
        return ResponseEntity.ok(cartResponse);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            HttpServletRequest servletRequest
    ) {
        Cookie[] cookies = servletRequest.getCookies();
        if (cookies != null) {
            Optional<Cookie> cartCookie = Arrays.stream(cookies)
                    .filter(cookie -> CART_COOKIE_NAME.equals(cookie.getName()))
                    .findFirst();

            cartCookie.ifPresent(cookie -> cartService.deleteCart(cookie.getValue()));
        }
        return ResponseEntity.noContent().build();
    }

    private String getOrCreateCartId(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String cartId = null;

        // Try to find the cart ID from cookies
        if (cookies != null) {
            Optional<Cookie> cartCookie = Arrays.stream(cookies)
                    .filter(cookie -> CART_COOKIE_NAME.equals(cookie.getName()))
                    .findFirst();

            if (cartCookie.isPresent()) {
                cartId = cartCookie.get().getValue();
            }
        }

        // If no cart ID found, create a new one
        if (cartId == null) {
            cartId = CartServiceImpl.generateCartId();
            Cookie cartCookie = new Cookie(CART_COOKIE_NAME, cartId);
            cartCookie.setMaxAge(COOKIE_MAX_AGE);
            cartCookie.setPath("/");
            cartCookie.setHttpOnly(true);
            response.addCookie(cartCookie);
        }

        return cartId;
    }
} 