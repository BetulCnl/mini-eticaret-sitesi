package com.betul.productmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Data                                       //kategori tablosunda aynı isme sahip iki kategori olamaz
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //db kendi oto id atar
    private Long id;

    @NotBlank //notnull+""
    @Size(max = 100)
    private String name;

    private String description;
} 