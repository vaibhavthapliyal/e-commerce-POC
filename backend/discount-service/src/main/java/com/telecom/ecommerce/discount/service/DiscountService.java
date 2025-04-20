package com.telecom.ecommerce.discount.service;

import com.telecom.ecommerce.discount.dto.DiscountRequest;
import com.telecom.ecommerce.discount.dto.DiscountResponse;

import java.util.List;

public interface DiscountService {

    List<DiscountResponse> getAllActiveDiscounts();
    
    DiscountResponse getDiscountById(Long id);
    
    DiscountResponse getDiscountByProductId(Long productId);
    
    DiscountResponse createDiscount(DiscountRequest discountRequest);
    
    DiscountResponse updateDiscount(Long id, DiscountRequest discountRequest);
    
    void deleteDiscount(Long id);
    
    void deactivateExpiredDiscounts();
} 