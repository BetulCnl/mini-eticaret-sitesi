package com.betul.productmanagement.controller;

import com.betul.productmanagement.dto.CartItemDTO;
import com.betul.productmanagement.dto.CartItemRequest;
import com.betul.productmanagement.dto.UpdateQuantityRequest;
import com.betul.productmanagement.entity.User;
import com.betul.productmanagement.service.CartService;
import com.betul.productmanagement.service.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
//@PreAuthorize("hasAnyRole('USER', 'ADMIN')") // Geçici olarak devre dışı bırakıldı
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getCart(@RequestHeader("X-Device-Id") String deviceId) {
        User user = currentUserService.getCurrentUserOrThrow();
        return ResponseEntity.ok(cartService.getCartItems(user, deviceId));
    }

    @PostMapping
    public ResponseEntity<CartItemDTO> addItem(@Valid @RequestBody CartItemRequest request, @RequestHeader("X-Device-Id") String deviceId) {
        User user = currentUserService.getCurrentUserOrThrow();
        CartItemDTO item = cartService.addItem(user, request.getProductId(), request.getQuantity(), deviceId);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartItemDTO> updateQuantity(@PathVariable Long id, @Valid @RequestBody UpdateQuantityRequest request, @RequestHeader("X-Device-Id") String deviceId) {
        User user = currentUserService.getCurrentUserOrThrow();
        CartItemDTO item = cartService.updateQuantity(user, id, request.getQuantity(), deviceId);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeItem(@PathVariable Long id, @RequestHeader("X-Device-Id") String deviceId) {
        logger.info("Attempting to remove item with ID: {} for device: {}", id, deviceId);
        User user = currentUserService.getCurrentUserOrThrow();
        cartService.removeItem(user, id, deviceId);
        logger.info("Item with ID {} removed successfully for user {} on device {}", id, user.getEmail(), deviceId);
        return ResponseEntity.noContent().build();
    }
}