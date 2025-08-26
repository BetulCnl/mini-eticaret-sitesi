package com.betul.productmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;
import java.util.ArrayList;
import com.betul.productmanagement.enums.Role;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Data       //aynı maile sahip iki yer olamaz. yani aynı maile sahip iki insan olamaz
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id //primarykey
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank //nnot null + "" olmayacak
    private String name;

    @Email //girilen değer email formatında mı diye kontrol ediyor
    @NotBlank
    private String email;

    @Column(name = "password_hash") //password_hash adında bir sütun ekliyorum db ye
    private String passwordHash;

    //**************************************
    @Enumerated(EnumType.STRING) //enumu db ye nasıl kaydedeceğini belirtiyor
    private Role role;
    //**************************************

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference   // parent taraf
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Order> orders = new ArrayList<>();

   //Kullanıcı nesnesinin role alanı bu enum tipinde olduğu için
}   //sadece bu iki değerden biri atanabilir.

