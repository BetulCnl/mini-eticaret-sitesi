package com.betul.productmanagement.entity;

//CartItem, bir kullanıcının sepetine eklediği her bir ürünü temsil ediyor
//her CartItem bir kullanıcıya bağlı. ve her CartItem bir ürünü işaret ediyor.
//hangi kullanıcının sepettte ne ürünü var diye bakıyor yani
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;


//Veritabanı aynı kullanıcı için aynı üründen birden fazla satır olmasına izin verir.
//Aynı ürün, sepete birden fazla defa eklenebilir, her ekleme ayrı satır olur.
//bu yukarda açıkladığımı sonradan anladım.
//uniqueConstraints ile tek satırda quantity sayısını belirtiyorum
//ayrı satırlar açmıyorum her quantity işte (her aynı order) için.
@Entity
@Table(name = "cart_items", uniqueConstraints = {
       @UniqueConstraint(columnNames = {"user_id", "product_id"})
})
//aynı kullanıcı için aynı üründen sadece bir satır olur,
// ama o satırda quantity değiştirilebilir.



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//dbye oto ekle
    private Long id;



    //bir user bir çok CartItema(sepete eklenen ürüne) sahip olabilir. bir Cartitem bir usera ait olabilir.
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    //optional = false -> her CartItem illa bir usera ait olacak, pas geçmeyecek (null olmayacak
    //fetch = FetchType.LAZY -> tüm kullanıcı bilgileri CartItem her çalıştığında gelmesine gerek yok bunun içn
//@JoinColumn(name = "user_id") → Veritabanında cart_items tablosunda user_id sütunu foreign key olarak kullanılır.


    //bir product bir çok CartItem içerisinde olabilir, ama bir CartItem sadece bir product ile ilişkilidir
    @ManyToOne(optional = false, fetch = FetchType.EAGER)//her CartItem illa bir producta bağlı olacak yoksa ürün olmadan nasıl eklenmiş ürün olsun
    @JoinColumn(name = "product_id") //foreign key olarak sütun eklendi
    private Product product;


    @Min(1)
    private int quantity;
    //bu quantity işte sepetteki ürün miktarını belirtiyor ama
    //burda karıştırdığım şey şu
    //quantity sepetteki aynı olan ürün sayısını mı belirtiyor yoksa,
    //sepette varolan tüm ürünlerin sayısını mı anlamıyorum
    //sepette aynı olan ürünleri mesela 2 telefon bunu ayrı ayrı satırlar olarak göstermiyor 2 telefon diye gösteriyor.
}

//KISACASI CARTIREM ENTITYSİ HANGİ KULLANICININ HANGİ ÜRÜNÜNÜN KAÇ ADET OLDUĞUNU TUTUYOR