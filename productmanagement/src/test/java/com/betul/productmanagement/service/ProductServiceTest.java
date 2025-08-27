package com.betul.productmanagement.service;

import com.betul.productmanagement.dto.ProductCreateDTO;
import com.betul.productmanagement.entity.Category;
import com.betul.productmanagement.entity.Product;
import com.betul.productmanagement.repository.CategoryRepository;
import com.betul.productmanagement.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mockito hazırla
    }

    @Test
    void saveProduct_ShouldSaveAndReturnProduct() {
        // Arrange
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setName("Laptop");
        dto.setDescription("Gaming Laptop");
        dto.setPrice(BigDecimal.valueOf(1500));
        dto.setCategoryName("Electronics");

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Product product = new Product();
        product.setId(1L);
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(category);

        when(categoryRepository.findByName("Electronics"))
                .thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        // Act
        Product saved = productService.saveProduct(dto);

        // Assert
        assertNotNull(saved);
        assertEquals("Laptop", saved.getName());
        assertEquals("Electronics", saved.getCategory().getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }
}
