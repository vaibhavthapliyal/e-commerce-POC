package com.telecom.ecommerce.apigateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/product-proxy")
public class ProductProxyController {

    private static final Logger log = LoggerFactory.getLogger(ProductProxyController.class);
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * Direct proxy to product service for debugging purposes
     */
    @GetMapping
    public ResponseEntity<Object> getProducts(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String dataAllowance,
            @RequestParam(required = false) String brand,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "popularity") String sort
    ) {
        log.info("Proxying product request with parameters: type={}, maxPrice={}, dataAllowance={}, brand={}, page={}, size={}, sort={}",
                type, maxPrice, dataAllowance, brand, page, size, sort);
        
        String url = "http://product-service/api/products/";
        
        // Add query parameters
        StringBuilder queryParams = new StringBuilder("?");
        if (type != null) queryParams.append("type=").append(type).append("&");
        if (maxPrice != null) queryParams.append("maxPrice=").append(maxPrice).append("&");
        if (dataAllowance != null) queryParams.append("dataAllowance=").append(dataAllowance).append("&");
        if (brand != null) queryParams.append("brand=").append(brand).append("&");
        queryParams.append("page=").append(page).append("&");
        queryParams.append("size=").append(size).append("&");
        queryParams.append("sort=").append(sort);
        
        url += queryParams.toString();
        
        try {
            log.info("Calling product service at: {}", url);
            ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
            log.info("Received response from product service: {}", response.getStatusCode());
            return response;
        } catch (Exception e) {
            log.error("Error proxying request to product service", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to proxy request");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable Long id) {
        log.info("Proxying request for product with ID: {}", id);
        String url = "http://product-service/api/products/" + id;
        
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
            return response;
        } catch (Exception e) {
            log.error("Error proxying product by ID request", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to proxy request");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
} 