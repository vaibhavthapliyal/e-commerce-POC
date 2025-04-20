package com.telecom.ecommerce.product.repository;

import com.telecom.ecommerce.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByType(String type, Pageable pageable);

    Page<Product> findByPriceLessThanEqual(BigDecimal maxPrice, Pageable pageable);

    Page<Product> findByDataAllowance(String dataAllowance, Pageable pageable);

    Page<Product> findByBrand(String brand, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
            "(:type IS NULL OR p.type = :type) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:dataAllowance IS NULL OR p.dataAllowance = :dataAllowance) AND " +
            "(:brand IS NULL OR p.brand = :brand)")
    Page<Product> findByFilters(
            @Param("type") String type,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("dataAllowance") String dataAllowance,
            @Param("brand") String brand,
            Pageable pageable
    );
} 