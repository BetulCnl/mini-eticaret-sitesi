package com.betul.productmanagement.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import java.util.List;
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


    @Test
    void getAllProducts_ShouldReturnPagedProducts() {
        //-------ARRANGE-------
        // Test için örnek ürünler oluştur
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Mouse");

        // Mock repository'nin döndüreceği Page nesnesini oluştur
        Page<Product> mockPage = new PageImpl<>(List.of(product1, product2));

        // Mock repository davranışını ayarla
        when(productRepository.findByCategory_Name(eq("Electronics"), any(Pageable.class)))
                .thenReturn(mockPage);

        //-------ACT (ÇALIŞTIR)-------
        Page<Product> result = productService.getAllProducts(0, 5, "Electronics");

        //-------ASSERT (DOĞRULA)-------
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("Laptop", result.getContent().get(0).getName());
        verify(productRepository, times(1)).findByCategory_Name(eq("Electronics"), any(Pageable.class));
    }

    @Test
    void searchByName_ShouldReturnProducts() {
        // Arrange
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Mouse");

        Page<Product> mockPage = new PageImpl<>(List.of(product1, product2));

        when(productRepository.findByNameContainingIgnoreCase(eq("top"), any(Pageable.class)))
                .thenReturn(mockPage);

        // Act
        Page<Product> result = productService.searchByName("top", 0, 5);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(productRepository, times(1)).findByNameContainingIgnoreCase(eq("top"), any(Pageable.class));
    }


    //-------------------------getProductById metodu için mesela 2 aşamalı test yazdık-------------------------
    // Birincisi ürün varsa, ikincisi ürün yoksa yani kodu değil koddaki durumları test etmiş gibi oluyor??
    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        Optional<Product> result = productService.getProductById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Laptop", result.get().getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_WhenProductDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productService.getProductById(1L);

        // Assert
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findById(1L);
    }
    //-----getProductById metodu için testler bitti-----


    @Test
    void deleteProduct_ShouldInvokeRepositoryDelete() {
        // Arrange
        Long productId = 1L;
        doNothing().when(productRepository).deleteById(productId);

        // Act
        productService.deleteProduct(productId);

        // Assert
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    void updateStock_ShouldUpdateAndReturnProduct() {
        // Arrange
        Long productId = 1L;
        int newStock = 50;

        Product product = new Product();
        product.setId(productId);
        product.setName("Laptop");
        product.setStock(20);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product updatedProduct = productService.updateStock(productId, newStock);

        // Assert
        assertNotNull(updatedProduct);
        assertEquals(newStock, updatedProduct.getStock());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_ShouldUpdateAndReturnProduct() {
        // Arrange
        Long productId = 1L;
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setName("Updated Laptop");
        dto.setDescription("Updated Description");
        dto.setPrice(BigDecimal.valueOf(2000));
        dto.setCategoryName("Electronics");

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Old Laptop");
        existingProduct.setDescription("Old Description");
        existingProduct.setPrice(BigDecimal.valueOf(1500));
        existingProduct.setCategory(category);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        // Act
        Product updatedProduct = productService.updateProduct(productId, dto);

        // Assert
        assertNotNull(updatedProduct);
        assertEquals("Updated Laptop", updatedProduct.getName());
        assertEquals("Updated Description", updatedProduct.getDescription());
        assertEquals(BigDecimal.valueOf(2000), updatedProduct.getPrice());
        assertEquals("Electronics", updatedProduct.getCategory().getName());
        verify(productRepository, times(1)).findById(productId);
        verify(categoryRepository, times(1)).findByName("Electronics");
        verify(productRepository, times(1)).save(any(Product.class));
    }



}
