package com.telecom.ecommerce.discount.repository;

import com.telecom.ecommerce.discount.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    List<Discount> findByActiveTrue();
    
    List<Discount> findByActiveTrueAndExpiryTimeAfter(LocalDateTime now);
    
    Optional<Discount> findByProductIdAndActiveTrue(Long productId);
    
    @Query("SELECT d FROM Discount d WHERE d.expiryTime < ?1 AND d.active = true")
    List<Discount> findExpiredDiscounts(LocalDateTime now);
} 