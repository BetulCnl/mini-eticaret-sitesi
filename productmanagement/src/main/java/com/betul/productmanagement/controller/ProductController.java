package com.betul.productmanagement.controller;

import com.betul.productmanagement.dto.ProductCreateDTO;
import com.betul.productmanagement.dto.UpdateStockRequest;
import com.betul.productmanagement.entity.Product;
import com.betul.productmanagement.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);


    // Tüm ürünleri listele
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String category) {
        logger.info("Fetching all products. Page: {}, Size: {}, Category: {}", page, size, category);
        Page<Product> products = productService.getAllProducts(page, size, category);
        logger.info("Found {} products on page {}. Total elements: {}", products.getContent().size(), products.getNumber(), products.getTotalElements());
        return ResponseEntity.ok(products);
    }

    // İsim ile ürün ara
    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<Product> products = productService.searchByName(name, page, size);
        return ResponseEntity.ok(products);
    }

    // ID ile ürün getir
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Ürün ekle (DTO kullanılıyor)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> addProduct(@Valid @RequestBody ProductCreateDTO dto) {
        Product saved = productService.saveProduct(dto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // ID ile ürün sil
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productService.getProductById(id).isPresent()) {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateStock(@PathVariable Long id, @RequestBody UpdateStockRequest request) {
        Product updatedProduct = productService.updateStock(id, request.getStock());
        return ResponseEntity.ok(updatedProduct);
    }


    //ID ile ürün güncelle
    @PostMapping("/{id}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductCreateDTO dto) {
        Product updatedProduct= productService.updateProduct(id, dto);
        return ResponseEntity.ok(updatedProduct);
    }


}
