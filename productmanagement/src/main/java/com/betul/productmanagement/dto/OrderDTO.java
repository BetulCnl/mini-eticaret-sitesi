package com.betul.productmanagement.dto;

import com.betul.productmanagement.entity.Order;
import com.betul.productmanagement.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private Instant createdAt;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private List<OrderItemDTO> items;
    private Long userId;
}