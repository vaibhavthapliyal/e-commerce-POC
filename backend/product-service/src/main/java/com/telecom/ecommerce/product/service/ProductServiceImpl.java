package com.telecom.ecommerce.product.service;

import com.telecom.ecommerce.product.dto.ProductPage;
import com.telecom.ecommerce.product.dto.ProductRequest;
import com.telecom.ecommerce.product.dto.ProductResponse;
import com.telecom.ecommerce.product.model.Product;
import com.telecom.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    
    // Manually define logger if @Slf4j doesn't work
    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    
    private final ProductRepository productRepository;
    
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = mapToEntity(productRequest);
        Product savedProduct = productRepository.save(product);
        log.info("Product created: {}", savedProduct.getId());
        return mapToResponse(savedProduct);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        return mapToResponse(product);
    }

    @Override
    public ProductPage getAllProducts(int page, int size, String sort) {
        Pageable pageable = createPageable(page, size, sort);
        Page<Product> productPage = productRepository.findAll(pageable);
        return createProductPage(productPage);
    }

    @Override
    public ProductPage getProductsByFilters(
            String type,
            BigDecimal maxPrice,
            String dataAllowance,
            String brand,
            int page,
            int size,
            String sort
    ) {
        Pageable pageable = createPageable(page, size, sort);
        Page<Product> productPage = productRepository.findByFilters(type, maxPrice, dataAllowance, brand, pageable);
        return createProductPage(productPage);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        // Update fields
        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setType(productRequest.getType());
        existingProduct.setImageUrl(productRequest.getImageUrl());
        existingProduct.setDataAllowance(productRequest.getDataAllowance());
        existingProduct.setBrand(productRequest.getBrand());

        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Product updated: {}", updatedProduct.getId());
        return mapToResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        log.info("Product deleted: {}", id);
    }

    private Pageable createPageable(int page, int size, String sort) {
        Sort.Direction direction = sort.startsWith("price-high-low") ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = getSortField(sort);
        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }

    private String getSortField(String sort) {
        if (sort.startsWith("price")) {
            return "price";
        } else if (sort.equals("newest")) {
            return "createdAt";
        } else {
            // Default to id for "popularity" or any other value
            return "id";
        }
    }

    private ProductPage createProductPage(Page<Product> productPage) {
        List<ProductResponse> productResponses = productPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        // Manual builder implementation if Lombok's builder isn't working
        ProductPage page = new ProductPage();
        page.setProducts(productResponses);
        page.setCurrentPage(productPage.getNumber());
        page.setTotalPages(productPage.getTotalPages());
        page.setTotalElements(productPage.getTotalElements());
        return page;
    }

    private Product mapToEntity(ProductRequest productRequest) {
        // Manual builder implementation if Lombok's builder isn't working
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setType(productRequest.getType());
        product.setImageUrl(productRequest.getImageUrl());
        product.setDataAllowance(productRequest.getDataAllowance());
        product.setBrand(productRequest.getBrand());
        return product;
    }

    private ProductResponse mapToResponse(Product product) {
        // Manual builder implementation if Lombok's builder isn't working
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setType(product.getType());
        response.setImageUrl(product.getImageUrl());
        response.setDataAllowance(product.getDataAllowance());
        response.setBrand(product.getBrand());
        return response;
    }
} 