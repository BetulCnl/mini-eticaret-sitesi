package com.betul.productmanagement.Mapper;

import com.betul.productmanagement.dto.OrderDTO;
import com.betul.productmanagement.dto.OrderItemDTO;
import com.betul.productmanagement.entity.Order;
import com.betul.productmanagement.entity.OrderItem;

import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderDTO toOrderDTO(Order order) { // OrderItem -> OrderItemDTO dönüşümünü yapan metot
        if (order == null) {
            return null;
        }

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setUserId(order.getUser().getId());

        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream()
                    .map(OrderMapper::toOrderItemDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public static OrderItemDTO toOrderItemDTO(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(orderItem.getProductId());
        dto.setProductName(orderItem.getProductName());
        dto.setUnitPrice(orderItem.getUnitPrice());
        dto.setQuantity(orderItem.getQuantity());

        return dto;
    }
}
