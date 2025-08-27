package com.betul.productmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import com.betul.productmanagement.enums.OrderStatus;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    //@JsonManagedReference *DTO KULLANDIĞIM İÇİN BUNA GEREK YOK SİLDİM
    @JoinColumn(name = "user_id")
    private User user;

    private Instant createdAt;

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING) // Enum string olarak DB'ye yazılacak
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //!!!EAGER yerine LAZY kullanıldı, çünkü order ile ilişkili orderItem'lar çok fazla olabileceğinden ve performans için lazy loading tercih edildi.
    @ToString.Exclude     //<------------- To avoid infinite recursion in toString
    @EqualsAndHashCode.Exclude //<------------- To avoid infinite recursion in equals and hashCode
    private List<OrderItem> items = new ArrayList<>();


    //BU ORDER DOSYASINDAKİ TRANSACTIONLARIN HEPSİ PRODUCT MANAGEMENT SERVİSİNDE YER ALACAK
    // SİLDİM BURDAKİ LİSTEYİ

    //TODO: orderStatus ekle: PENDING, CANCELLED, COMPLETED, FAILED - TAMAMLANDI
    //TODO: USERS VE ORDERS arasında stackoverflow hatası - TAMAMLANDI
    //TODO: User login olurken kullanıcı ismini mailini clienta dön ve headerda bu değerleri göster - TAMAMLANDI
    //TODO: İhtiyaçlar: purchase sayfası olacak. purchase sayfasında iki buton, biri success diğeri fail ödeme geçmek. - TAMAMLANDI
    //TODO: PaymentTransaction entitysi ekle, bu entity siparişin durumunu tutacak. status: Success, Failed, Pending - TAMAMLANDI
    //TODO: payment işlemini ayrı bir payment management microserviceinde yapacağız feign client ile. - TAMAMLANDI
    //TODO: myOrders sayfası olacak. orda siparişlerin listelenecek. - TAMAMLANDI
    //TODO: myOrders sayfasında completed olan bir sipariş iptal edilebilecek. - TAMAMLANDI
}