package com.telecom.ecommerce.discount.service;

import com.telecom.ecommerce.discount.dto.DiscountRequest;
import com.telecom.ecommerce.discount.dto.DiscountResponse;
import com.telecom.ecommerce.discount.model.Discount;
import com.telecom.ecommerce.discount.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DiscountServiceImpl implements DiscountService {

    private static final Logger log = LoggerFactory.getLogger(DiscountServiceImpl.class);
    private final DiscountRepository discountRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Autowired
    public DiscountServiceImpl(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    @Override
    public List<DiscountResponse> getAllActiveDiscounts() {
        List<Discount> discounts = discountRepository.findByActiveTrueAndExpiryTimeAfter(LocalDateTime.now());
        return discounts.stream()
                .map(this::mapToDiscountResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DiscountResponse getDiscountById(Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Discount not found with id: " + id));
        return mapToDiscountResponse(discount);
    }

    @Override
    public DiscountResponse getDiscountByProductId(Long productId) {
        Discount discount = discountRepository.findByProductIdAndActiveTrue(productId)
                .orElseThrow(() -> new EntityNotFoundException("No active discount found for product id: " + productId));
        
        // Check if the discount is expired
        if (discount.isExpired()) {
            discount.setActive(false);
            discountRepository.save(discount);
            throw new EntityNotFoundException("Discount for product id " + productId + " has expired");
        }
        
        return mapToDiscountResponse(discount);
    }

    @Override
    public DiscountResponse createDiscount(DiscountRequest discountRequest) {
        // Check if there's an existing active discount for the product
        discountRepository.findByProductIdAndActiveTrue(discountRequest.getProductId())
                .ifPresent(existingDiscount -> {
                    existingDiscount.setActive(false);
                    discountRepository.save(existingDiscount);
                });
        
        // Manual builder implementation instead of using lombok builder
        Discount discount = new Discount();
        discount.setProductId(discountRequest.getProductId());
        discount.setPercentage(discountRequest.getPercentage());
        discount.setExpiryTime(LocalDateTime.parse(discountRequest.getExpiryTime(), FORMATTER));
        
        Discount savedDiscount = discountRepository.save(discount);
        log.info("Created discount with id: {}", savedDiscount.getId());
        
        return mapToDiscountResponse(savedDiscount);
    }

    @Override
    public DiscountResponse updateDiscount(Long id, DiscountRequest discountRequest) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Discount not found with id: " + id));
        
        discount.setProductId(discountRequest.getProductId());
        discount.setPercentage(discountRequest.getPercentage());
        discount.setExpiryTime(LocalDateTime.parse(discountRequest.getExpiryTime(), FORMATTER));
        
        Discount updatedDiscount = discountRepository.save(discount);
        log.info("Updated discount with id: {}", updatedDiscount.getId());
        
        return mapToDiscountResponse(updatedDiscount);
    }

    @Override
    public void deleteDiscount(Long id) {
        if (!discountRepository.existsById(id)) {
            throw new EntityNotFoundException("Discount not found with id: " + id);
        }
        
        discountRepository.deleteById(id);
        log.info("Deleted discount with id: {}", id);
    }

    @Override
    @Scheduled(fixedRate = 60000) // Run every minute
    public void deactivateExpiredDiscounts() {
        List<Discount> expiredDiscounts = discountRepository.findExpiredDiscounts(LocalDateTime.now());
        
        if (!expiredDiscounts.isEmpty()) {
            expiredDiscounts.forEach(discount -> discount.setActive(false));
            discountRepository.saveAll(expiredDiscounts);
            
            log.info("Deactivated {} expired discounts", expiredDiscounts.size());
        }
    }
    
    private DiscountResponse mapToDiscountResponse(Discount discount) {
        // Manual builder implementation instead of using lombok builder
        DiscountResponse response = new DiscountResponse();
        response.setId(discount.getId());
        response.setProductId(discount.getProductId());
        response.setPercentage(discount.getPercentage());
        response.setExpiryTime(discount.getExpiryTime().format(FORMATTER));
        response.setActive(discount.isActive());
        return response;
    }
} 