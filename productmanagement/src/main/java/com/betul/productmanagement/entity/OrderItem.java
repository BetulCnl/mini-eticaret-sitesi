package com.betul.productmanagement.entity;

import com.betul.productmanagement.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //*********************
    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    @ToString.Exclude //<------------- To avoid infinite recursion in toString
    @EqualsAndHashCode.Exclude //<------------- To avoid infinite recursion in equals and hashCode
    @JsonBackReference
    //*********************** bu kısmı bak
    private Order order;
    // We add productId to keep a reference to the original product,
    // even if it's deleted from the products table later.
    private Long productId;

    private String productName;

    private BigDecimal unitPrice;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    //product id ekle, product entitysi ile ilişki kurma. -> DONE
    //Neden? Çünkü ürün silinse bile sipariş geçmişinde ürünün ne olduğunu bilmek isteriz.
    //Ama ürünün fiyatı değişebilir, o yüzden unitPrice'ı burada tutuyoruz.
    //Bu sayede sipariş anındaki fiyatı korumuş oluyoruz.
    //productName de aynı şekilde, ürünün adı değişse bile sipariş anındaki adını korumak için.
    //Bu işleme "snapshotting" denir. Sipariş anının fotoğrafını çekmek gibi.
    //product idyi de ekleyerek, ürün silinmemişse detayına gidebilme imkanı sunabiliriz.
}


//ŞİMDİ SEPETTE YAPTIĞIMIZ GİBİ DİREKT ENTİTY DÖNMEMEK İÇİN DTO LARI OLUŞTURUCAZ
//ORDERDTO VE ORDERITEMDTO