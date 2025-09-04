package com.betul.productmanagement.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;  // userId yerine User nesnesi

    private String deviceId;

    private String token;

    private LocalDateTime createdAt; // expiry yerine createdAt
}

//Login sırasında, kullanıcı giriş yaptığında frontend cihazın benzersiz kimliğini (deviceId) backend’e gönderiyor.
// Backend, token üretirken userId ile deviceId’yi ilişkilendiriyor ve session tablosunda saklıyor.
