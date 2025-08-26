package com.betul.productmanagement.config;

import com.betul.productmanagement.entity.Category;
import com.betul.productmanagement.entity.Product;
import com.betul.productmanagement.repository.CategoryRepository;
import com.betul.productmanagement.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Bean
    CommandLineRunner seedProducts() {
        return args -> {
            if (categoryRepository.count() == 0) {
                Category electronics = categoryRepository.save(Category.builder()
                        .name("Electronics").description("Electronic devices").build());
                Category home = categoryRepository.save(Category.builder()
                        .name("Home").description("Home & Kitchen").build());
                Category fashion = categoryRepository.save(Category.builder()
                        .name("Fashion").description("Clothing & Accessories").build());

                if (productRepository.count() == 0) {
                    productRepository.save(Product.builder()
                            .name("Wireless Mouse")
                            .description("2.4G Ergonomic Mouse")
                            .price(new BigDecimal("199.90"))
                            .stock(50)
                            .category(electronics)
                            .build());
                    productRepository.save(Product.builder()
                            .name("Mechanical Keyboard")
                            .description("RGB, Blue Switch")
                            .price(new BigDecimal("1499.00"))
                            .stock(20)
                            .category(electronics)
                            .build());
                    productRepository.save(Product.builder()
                            .name("Water Bottle")
                            .description("BPA-free 1L")
                            .price(new BigDecimal("89.99"))
                            .stock(100)
                            .category(home)
                            .build());

                    // extra samples
                    productRepository.save(Product.builder()
                            .name("Bluetooth Speaker")
                            .description("Portable")
                            .price(new BigDecimal("799.00"))
                            .stock(35)
                            .category(electronics)
                            .build());
                    productRepository.save(Product.builder()
                            .name("Air Fryer")
                            .description("4L")
                            .price(new BigDecimal("2499.00"))
                            .stock(15)
                            .category(home)
                            .build());
                    productRepository.save(Product.builder()
                            .name("Hoodie")
                            .description("Unisex, M")
                            .price(new BigDecimal("349.90"))
                            .stock(60)
                            .category(fashion)
                            .build());
                    productRepository.save(Product.builder()
                            .name("Sneakers")
                            .description("White, 42")
                            .price(new BigDecimal("899.00"))
                            .stock(25)
                            .category(fashion)
                            .build());
                }
            }
        };
    }
} 