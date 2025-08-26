package com.betul.productmanagement.Mapper;

import com.betul.productmanagement.dto.CartItemDTO;
import com.betul.productmanagement.dto.ProductDTO;
import com.betul.productmanagement.entity.CartItem;

public class CartMapper {

    public static CartItemDTO toCartItemDTO(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }

        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setId(cartItem.getId());
        cartItemDTO.setQuantity(cartItem.getQuantity());

        if (cartItem.getProduct() != null) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(cartItem.getProduct().getId());
            productDTO.setName(cartItem.getProduct().getName());
            productDTO.setPrice(cartItem.getProduct().getPrice());
            cartItemDTO.setProduct(productDTO);
        }

        return cartItemDTO;
    }
}
