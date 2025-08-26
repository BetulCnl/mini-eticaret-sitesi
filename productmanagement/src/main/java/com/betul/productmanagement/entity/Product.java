package com.betul.productmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "products")  //TODO: buraya name üxerinden index ekle
public class Product {

    @Id //idnin primary key olduğunu benersiz olduğunu belirtmek için kullandık, kullanmamız lazım
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Id yi db oto kaydetsin
    private Long id; //long daha büyük id aralığı olsun diye

    //optimistic locking
    @Version //Concurrency control sağlar, -> kritik veri tutarlılığı gereken yerlerde çok faydalıdır.
    private Long version;

    //trim().length() > 0 kontrolü yapar yani
    @NotBlank // bu a. hem @notnull gibi null değerine hem de boşluk karakterine("") izin vermez
    @Size(max = 100)
    private String name;

    @Size(max = 500) //max 500 karakter yazılsın. bu gerekli değildi öylesine koydum
    private String description;

    @NotNull //ürün kaydedilirken price hiç set edilmezse hata verecek program, 0 değeri alabilir hiçlik değeri alamaz
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal price;

    @Min(0) //stock minimum 0 olabilir - ye düşemez
    private int stock;

    @ManyToOne //ilişki tablosunu modellemek için kullandık.
    @JoinColumn(name = "category_id") //kategori id sütunu product tablosunda, mesela elektronik aletler id=1, ev eşyaları id=2 gibi
    private Category category;
}   //bir kategori birden fazla ürüne sahip olabilir. (bu ürünlerin kategori id si de aynı-

