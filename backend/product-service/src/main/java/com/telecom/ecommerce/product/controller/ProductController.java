package com.telecom.ecommerce.product.controller;

import com.telecom.ecommerce.product.dto.ProductPage;
import com.telecom.ecommerce.product.dto.ProductRequest;
import com.telecom.ecommerce.product.dto.ProductResponse;
import com.telecom.ecommerce.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        log.info("Received request to create product: {}", productRequest);
        ProductResponse productResponse = productService.createProduct(productRequest);
        log.info("Created product with ID: {}", productResponse.getId());
        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        log.info("Received request to get product with ID: {}", id);
        ProductResponse productResponse = productService.getProductById(id);
        log.info("Returning product: {}", productResponse);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping
    public ResponseEntity<ProductPage> getProducts(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String dataAllowance,
            @RequestParam(required = false) String brand,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "popularity") String sort
    ) {
        log.info("Received request to get products with filters: type={}, maxPrice={}, dataAllowance={}, brand={}, page={}, size={}, sort={}",
                type, maxPrice, dataAllowance, brand, page, size, sort);
        
        ProductPage productPage;
        if (type == null && maxPrice == null && dataAllowance == null && brand == null) {
            // No filters applied, get all products
            productPage = productService.getAllProducts(page, size, sort);
        } else {
            // Apply filters
            productPage = productService.getProductsByFilters(
                    type, maxPrice, dataAllowance, brand, page, size, sort
            );
        }
        
        log.info("Returning product page with {} products, page {}/{}, total elements: {}",
                productPage.getProducts().size(), productPage.getCurrentPage() + 1, 
                productPage.getTotalPages(), productPage.getTotalElements());
        
        return ResponseEntity.ok(productPage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest productRequest
    ) {
        log.info("Received request to update product with ID: {}", id);
        ProductResponse productResponse = productService.updateProduct(id, productRequest);
        log.info("Updated product: {}", productResponse);
        return ResponseEntity.ok(productResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("Received request to delete product with ID: {}", id);
        productService.deleteProduct(id);
        log.info("Deleted product with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
} 