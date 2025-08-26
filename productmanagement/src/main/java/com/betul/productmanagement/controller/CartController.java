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

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class CartController {

    private final CartService cartService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getCart() {
        User user = currentUserService.getCurrentUserOrThrow();
        return ResponseEntity.ok(cartService.getCartItems(user));
    }

    @PostMapping
    public ResponseEntity<CartItemDTO> addItem(@Valid @RequestBody CartItemRequest request) {
        User user = currentUserService.getCurrentUserOrThrow();
        CartItemDTO item = cartService.addItem(user, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartItemDTO> updateQuantity(@PathVariable Long id, @Valid @RequestBody UpdateQuantityRequest request) {
        User user = currentUserService.getCurrentUserOrThrow();
        CartItemDTO item = cartService.updateQuantity(user, id, request.getQuantity());
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeItem(@PathVariable Long id) {
        User user = currentUserService.getCurrentUserOrThrow();
        cartService.removeItem(user, id);
        return ResponseEntity.noContent().build();
    }
} 