package com.betul.productmanagement.repository;

import com.betul.productmanagement.entity.CartItem;
import com.betul.productmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserAndDeviceId(User user, String deviceId);
    Optional<CartItem> findByIdAndUserIdAndDeviceId(Long id, Long userId, String deviceId);
    Optional<CartItem> findByUserIdAndProductIdAndDeviceId(Long userId, Long productId, String deviceId);
    void deleteByUserAndDeviceId(User user, String deviceId);
} 