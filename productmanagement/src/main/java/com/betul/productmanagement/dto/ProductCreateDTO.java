package com.betul.productmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ProductCreateDTO {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private BigDecimal price;

    @NotNull
    private int stock; // Added stock field

    @NotBlank
    private String categoryName; // Sadece kategori adı alınacak

    // Getters ve Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getStock() { return stock; } // Added getter for stock
    public void setStock(int stock) { this.stock = stock; } // Added setter for stock

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}

