package com.telecom.ecommerce.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FallbackController {

    // Generic fallback endpoint for all services
    @GetMapping("/fallback")
    public Mono<ResponseEntity<Map<String, Object>>> genericFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "The service is temporarily unavailable. Please try again later.");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }

    // Specific service fallbacks
    @GetMapping("/api/fallback/products")
    public ResponseEntity<Map<String, Object>> productFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product service is currently unavailable. Please try again later.");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    @GetMapping("/api/fallback/cart")
    public ResponseEntity<Map<String, Object>> cartFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Cart service is currently unavailable. Please try again later.");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    @GetMapping("/api/fallback/discounts")
    public ResponseEntity<Map<String, Object>> discountFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Discount service is currently unavailable. Please try again later.");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    @GetMapping("/api/fallback/payments")
    public ResponseEntity<Map<String, Object>> paymentFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Payment service is currently unavailable. Please try again later.");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    @GetMapping("/api/fallback/notifications")
    public ResponseEntity<Map<String, Object>> notificationFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Notification service is currently unavailable. Please try again later.");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
} 