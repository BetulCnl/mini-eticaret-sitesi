package com.betul.productmanagement.service;

import com.betul.productmanagement.dto.ProductCreateDTO;
import com.betul.productmanagement.entity.Category;
import com.betul.productmanagement.entity.Product;
import com.betul.productmanagement.repository.CategoryRepository;
import com.betul.productmanagement.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // DTO ile ürün kaydetme (kategori kontrolü dahil)
    @CacheEvict(value = "products", allEntries = true)
    public Product saveProduct(ProductCreateDTO dto) {
        Category category = categoryRepository.findByName(dto.getCategoryName())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(category);

        return productRepository.save(product);
    }


    //cacheleme için buraya debug eklemiştik
    // Tüm ürünleri listele (sayfalama opsiyonel)
    @Cacheable("products")
    public Page<Product> getAllProducts(int page, int size, String categoryName) {
        Pageable pageable = PageRequest.of(page, size); //<-----------------
        if (categoryName != null && !categoryName.isBlank()) {
            return productRepository.findByCategory_Name(categoryName, pageable);
        }
        return productRepository.findAll(pageable);
    }

    // İsim ile ürün ara
    @Cacheable("products")
    public Page<Product> searchByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    // ID ile ürün getir
    @Cacheable(value = "products", key = "#id")
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // ID ile ürün sil
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}


