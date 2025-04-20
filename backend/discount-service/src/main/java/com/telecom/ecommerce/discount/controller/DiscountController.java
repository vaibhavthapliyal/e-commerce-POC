package com.telecom.ecommerce.discount.controller;

import com.telecom.ecommerce.discount.dto.DiscountRequest;
import com.telecom.ecommerce.discount.dto.DiscountResponse;
import com.telecom.ecommerce.discount.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    private final DiscountService discountService;
    
    @Autowired
    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @GetMapping
    public ResponseEntity<List<DiscountResponse>> getAllActiveDiscounts() {
        List<DiscountResponse> discounts = discountService.getAllActiveDiscounts();
        return ResponseEntity.ok(discounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiscountResponse> getDiscountById(@PathVariable Long id) {
        DiscountResponse discount = discountService.getDiscountById(id);
        return ResponseEntity.ok(discount);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<DiscountResponse> getDiscountByProductId(@PathVariable Long productId) {
        DiscountResponse discount = discountService.getDiscountByProductId(productId);
        return ResponseEntity.ok(discount);
    }

    @PostMapping
    public ResponseEntity<DiscountResponse> createDiscount(@Valid @RequestBody DiscountRequest discountRequest) {
        DiscountResponse createdDiscount = discountService.createDiscount(discountRequest);
        return new ResponseEntity<>(createdDiscount, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiscountResponse> updateDiscount(
            @PathVariable Long id,
            @Valid @RequestBody DiscountRequest discountRequest
    ) {
        DiscountResponse updatedDiscount = discountService.updateDiscount(id, discountRequest);
        return ResponseEntity.ok(updatedDiscount);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.noContent().build();
    }
} 