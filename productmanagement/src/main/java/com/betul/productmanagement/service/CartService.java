package com.betul.productmanagement.service;

import com.betul.productmanagement.Mapper.CartMapper;
import com.betul.productmanagement.dto.CartItemDTO;
import com.betul.productmanagement.entity.CartItem;
import com.betul.productmanagement.entity.Product;
import com.betul.productmanagement.entity.User;
import com.betul.productmanagement.exception.InvalidQuantityException;
import com.betul.productmanagement.repository.CartItemRepository;
import com.betul.productmanagement.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    /**
     * Kullanıcının belirli bir cihazdaki sepetindeki ürünleri listeler.
     */
    public List<CartItemDTO> getCartItems(User user, String deviceId) {
        return cartItemRepository.findByUserAndDeviceId(user, deviceId).stream()
                .map(CartMapper::toCartItemDTO)
                .collect(Collectors.toList());
    }

    /**
     * Sepete ürün ekler. Eğer ürün zaten varsa miktarını artırır.
     */
    @Transactional
    public CartItemDTO addItem(User user, Long productId, int quantity, String deviceId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new InvalidQuantityException("Ürün bulunamadı."));

        if (quantity < 1 || quantity > product.getStock()) {
            throw new InvalidQuantityException(
                    "Miktar 1 ile " + product.getStock() + " arasında olmalıdır."
            );
        }

        CartItem cartItem = cartItemRepository.findByUserIdAndProductIdAndDeviceId(user.getId(), productId, deviceId)
                .map(existing -> {
                    int newQty = existing.getQuantity() + quantity;
                    if (newQty > product.getStock()) {
                        throw new InvalidQuantityException(
                                "Toplam miktar stoktan fazla olamaz (" + product.getStock() + ")."
                        );
                    }
                    existing.setQuantity(newQty);
                    return cartItemRepository.save(existing);
                })
                .orElseGet(() -> {
                    CartItem newItem = CartItem.builder()
                            .user(user)
                            .product(product)
                            .quantity(quantity)
                            .deviceId(deviceId) // deviceId eklendi
                            .build();
                    return cartItemRepository.save(newItem);
                });
        return CartMapper.toCartItemDTO(cartItem);
    }

    /**
     * Sepetteki bir ürünün miktarını günceller.
     */
    @Transactional
    public CartItemDTO updateQuantity(User user, Long cartItemId, int quantity, String deviceId) {
        CartItem item = cartItemRepository.findByIdAndUserIdAndDeviceId(cartItemId, user.getId(), deviceId)
                .orElseThrow(() -> new InvalidQuantityException("Sepet öğesi bulunamadı."));

        int stock = item.getProduct().getStock();
        if (quantity < 1 || quantity > stock) {
            throw new InvalidQuantityException(
                    "Miktar 1 ile " + stock + " arasında olmalıdır."
            );
        }

        item.setQuantity(quantity);
        return CartMapper.toCartItemDTO(cartItemRepository.save(item));
    }

    /**
     * Sepetten bir ürünü tamamen kaldırır.
     */
    @Transactional
    public void removeItem(User user, Long cartItemId, String deviceId) {
        CartItem item = cartItemRepository.findByIdAndUserIdAndDeviceId(cartItemId, user.getId(), deviceId)
                .orElseThrow(() -> new InvalidQuantityException("Sepet öğesi bulunamadı."));
        cartItemRepository.delete(item);
    }

    /**
     * Kullanıcının belirli bir cihazdaki sepetini tamamen boşaltır.
     */
    @Transactional
    public void clearCart(User user, String deviceId) {
        // Not: Bu metodun repository'de karşılığı `deleteByUserAndDeviceId` olmalı.
        // Ancak bu metod tüm sepeti sildiği için, önce o cihaza ait tüm item'ları bulup silmek daha güvenli olabilir.
        // Şimdilik direkt repository metodunu çağıracak şekilde bırakıyorum.
        cartItemRepository.deleteByUserAndDeviceId(user, deviceId);
    }
}