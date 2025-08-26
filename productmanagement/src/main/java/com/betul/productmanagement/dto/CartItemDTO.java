package com.betul.productmanagement.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private Long id;
    private int quantity;
    private ProductDTO product;
}
//cartcontrollerda http isteği attığımda mesela get
// bana tüm cartentity i göstermeyecek
//carItemdto gösterilecek

//cartitemdto içinde productdto olacak
//productdto içinde productın id, name, price olacak
//CartItemDTO'nun bir ürüne mi ihtiyacı var? İçine bir ProductDTO koyar. ve o özel product için productdto getirilmesi gerekeni getirir