package com.telecom.ecommerce.product;

import com.telecom.ecommerce.product.model.Product;
import com.telecom.ecommerce.product.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
@EnableEurekaClient
public class ProductServiceApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter());
    }
    
    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository) {
        return args -> {
            // Check if database is empty
            if (productRepository.count() == 0) {
                System.out.println("Initializing product database with sample data");
                
                // Add tariff plans
                Product unlimitedPlan = new Product();
                unlimitedPlan.setName("Unlimited Data Plan");
                unlimitedPlan.setDescription("Unlimited data, calls and texts. Perfect for heavy users.");
                unlimitedPlan.setPrice(new BigDecimal("49.99"));
                unlimitedPlan.setType("tariff");
                unlimitedPlan.setImageUrl("https://via.placeholder.com/300x200?text=Unlimited+Plan");
                unlimitedPlan.setDataAllowance("Unlimited");
                unlimitedPlan.setCreatedAt(LocalDateTime.now());
                unlimitedPlan.setUpdatedAt(LocalDateTime.now());
                productRepository.save(unlimitedPlan);
                
                Product basicPlan = new Product();
                basicPlan.setName("5G Basic Plan");
                basicPlan.setDescription("10GB data with 5G speeds, unlimited calls and texts.");
                basicPlan.setPrice(new BigDecimal("29.99"));
                basicPlan.setType("tariff");
                basicPlan.setImageUrl("https://via.placeholder.com/300x200?text=5G+Basic+Plan");
                basicPlan.setDataAllowance("10GB");
                basicPlan.setCreatedAt(LocalDateTime.now());
                basicPlan.setUpdatedAt(LocalDateTime.now());
                productRepository.save(basicPlan);
                
                Product familyPlan = new Product();
                familyPlan.setName("Family Plan");
                familyPlan.setDescription("Share 30GB of data between 4 lines with unlimited calls and texts.");
                familyPlan.setPrice(new BigDecimal("79.99"));
                familyPlan.setType("tariff");
                familyPlan.setImageUrl("https://via.placeholder.com/300x200?text=Family+Plan");
                familyPlan.setDataAllowance("30GB");
                familyPlan.setCreatedAt(LocalDateTime.now());
                familyPlan.setUpdatedAt(LocalDateTime.now());
                productRepository.save(familyPlan);
                
                // Add devices
                Product iphone = new Product();
                iphone.setName("iPhone 13 Pro");
                iphone.setDescription("Latest iPhone with A15 Bionic chip and Pro camera system.");
                iphone.setPrice(new BigDecimal("999.99"));
                iphone.setType("device");
                iphone.setBrand("Apple");
                iphone.setImageUrl("https://via.placeholder.com/300x200?text=iPhone+13+Pro");
                iphone.setCreatedAt(LocalDateTime.now());
                iphone.setUpdatedAt(LocalDateTime.now());
                productRepository.save(iphone);
                
                Product samsung = new Product();
                samsung.setName("Samsung Galaxy S21");
                samsung.setDescription("Powerful Android phone with amazing camera and 5G.");
                samsung.setPrice(new BigDecimal("799.99"));
                samsung.setType("device");
                samsung.setBrand("Samsung");
                samsung.setImageUrl("https://via.placeholder.com/300x200?text=Samsung+S21");
                samsung.setCreatedAt(LocalDateTime.now());
                samsung.setUpdatedAt(LocalDateTime.now());
                productRepository.save(samsung);
                
                Product pixel = new Product();
                pixel.setName("Google Pixel 6");
                pixel.setDescription("Google's flagship phone with advanced AI capabilities.");
                pixel.setPrice(new BigDecimal("699.99"));
                pixel.setType("device");
                pixel.setBrand("Google");
                pixel.setImageUrl("https://via.placeholder.com/300x200?text=Google+Pixel+6");
                pixel.setCreatedAt(LocalDateTime.now());
                pixel.setUpdatedAt(LocalDateTime.now());
                productRepository.save(pixel);
                
                System.out.println("Sample product data initialized successfully");
            }
        };
    }
} 