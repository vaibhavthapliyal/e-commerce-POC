package com.telecom.ecommerce.discount;

import com.telecom.ecommerce.discount.model.Discount;
import com.telecom.ecommerce.discount.repository.DiscountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableEurekaClient
public class DiscountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscountServiceApplication.class, args);
    }
    
    @Bean
    CommandLineRunner initDatabase(DiscountRepository discountRepository) {
        return args -> {
            // Check if database is empty
            if (discountRepository.count() == 0) {
                System.out.println("Initializing discount database with sample data");
                
                // Add discounts for various products (assuming products with IDs 1 through 6 exist)
                
                // Discount for Unlimited Data Plan (Product ID 1) - 10% off
                Discount unlimitedPlanDiscount = new Discount();
                unlimitedPlanDiscount.setProductId(1L);
                unlimitedPlanDiscount.setPercentage(10);
                unlimitedPlanDiscount.setExpiryTime(LocalDateTime.now().plusDays(30));
                unlimitedPlanDiscount.setActive(true);
                unlimitedPlanDiscount.setCreatedAt(LocalDateTime.now());
                unlimitedPlanDiscount.setUpdatedAt(LocalDateTime.now());
                discountRepository.save(unlimitedPlanDiscount);
                
                // Discount for Samsung Galaxy S21 (Product ID 5) - 15% off
                Discount samsungDiscount = new Discount();
                samsungDiscount.setProductId(5L);
                samsungDiscount.setPercentage(15);
                samsungDiscount.setExpiryTime(LocalDateTime.now().plusDays(14));
                samsungDiscount.setActive(true);
                samsungDiscount.setCreatedAt(LocalDateTime.now());
                samsungDiscount.setUpdatedAt(LocalDateTime.now());
                discountRepository.save(samsungDiscount);
                
                System.out.println("Sample discount data initialized successfully");
            }
        };
    }
} 