package com.telecom.ecommerce.cart.client;

import com.telecom.ecommerce.cart.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", path = "/api/products")
public interface ProductServiceClient {

    @GetMapping("/{id}")
    ProductDto getProductById(@PathVariable("id") Long id);
} 