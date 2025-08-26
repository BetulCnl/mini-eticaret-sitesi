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
     * Kullanıcının sepetindeki ürünleri listeler.
     */
    public List<CartItemDTO> getCartItems(User user) {
        return cartItemRepository.findByUser(user).stream()
                .map(CartMapper::toCartItemDTO)
                .collect(Collectors.toList());
    }

    /**
     * Sepete ürün ekler. Eğer ürün zaten varsa miktarını artırır.
     */
    @Transactional
    public CartItemDTO addItem(User user, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new InvalidQuantityException("Ürün bulunamadı."));

        if (quantity < 1 || quantity > product.getStock()) {
            throw new InvalidQuantityException(
                    "Miktar 1 ile " + product.getStock() + " arasında olmalıdır."
            );
        }

        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(user.getId(), productId)
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
                            .build();
                    return cartItemRepository.save(newItem);
                });
        return CartMapper.toCartItemDTO(cartItem);
    }

    /**
     * Sepetteki bir ürünün miktarını günceller.
     */
    @Transactional
    public CartItemDTO updateQuantity(User user, Long cartItemId, int quantity) {
        CartItem item = cartItemRepository.findByIdAndUserId(cartItemId, user.getId())
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
    public void removeItem(User user, Long cartItemId) {
        CartItem item = cartItemRepository.findByIdAndUserId(cartItemId, user.getId())
                .orElseThrow(() -> new InvalidQuantityException("Sepet öğesi bulunamadı."));
        cartItemRepository.delete(item);
    }

    /**
     * Kullanıcının sepetini tamamen boşaltır.
     */
    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }
}
