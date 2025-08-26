package com.betul.productmanagement.repository;

import com.betul.productmanagement.entity.Product;
import com.betul.productmanagement.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Override
    @EntityGraph(attributePaths = {"category"})
    Page<Product> findAll(Pageable pageable);


    //"Bana ürünleri getirirken, onlara bağlı olan kategori bilgilerini de tek seferde, aynı sorgunun içinde getir."-> entity graph
    @EntityGraph(attributePaths = {"category"})
    Page<Product> findByCategory_Name(String categoryName, Pageable pageable);

    Page<Product> findByCategory(Category category, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findByPriceBetween(BigDecimal min, BigDecimal max, Pageable pageable);

    Page<Product> findByCategoryAndPriceBetween(String category, BigDecimal min, BigDecimal max, Pageable pageable);
}
