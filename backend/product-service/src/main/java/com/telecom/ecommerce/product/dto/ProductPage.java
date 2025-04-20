package com.telecom.ecommerce.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductPage implements Serializable {
    private List<ProductResponse> products;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    
    // Manual getter methods in case Lombok isn't working
    public List<ProductResponse> getProducts() {
        return products;
    }
    
    public int getCurrentPage() {
        return currentPage;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public long getTotalElements() {
        return totalElements;
    }
    
    // Manual setter methods in case Lombok isn't working
    public void setProducts(List<ProductResponse> products) {
        this.products = products;
    }
    
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
} 