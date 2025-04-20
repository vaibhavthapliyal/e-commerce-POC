package com.telecom.ecommerce.product.service;

import com.telecom.ecommerce.product.dto.ProductPage;
import com.telecom.ecommerce.product.dto.ProductRequest;
import com.telecom.ecommerce.product.dto.ProductResponse;

import java.math.BigDecimal;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productRequest);

    ProductResponse getProductById(Long id);

    ProductPage getAllProducts(int page, int size, String sort);

    ProductPage getProductsByFilters(
            String type,
            BigDecimal maxPrice,
            String dataAllowance,
            String brand,
            int page,
            int size,
            String sort
    );

    ProductResponse updateProduct(Long id, ProductRequest productRequest);

    void deleteProduct(Long id);
} 