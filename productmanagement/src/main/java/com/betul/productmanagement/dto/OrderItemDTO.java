package com.betul.productmanagement.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Long productId;
    private String productName;
    private BigDecimal unitPrice;
    private int quantity;
}
