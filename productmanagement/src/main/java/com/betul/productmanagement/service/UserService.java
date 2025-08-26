package com.betul.productmanagement.service;

import com.betul.productmanagement.dto.RegisterRequest;
import com.betul.productmanagement.dto.UserResponseDTO;
import com.betul.productmanagement.entity.User;
import com.betul.productmanagement.repository.UserRepository;
import com.betul.productmanagement.Mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

//bu service UserService: kullanıcı verisi oluşturma ve yönetme işlemi yapar.
//kullanıcı kayıt işlemleri, kullanıcı bilgilerini bulma ve DTO dönüşümleri gibi işlemleri içerir.

    private final UserRepository userRepository;
    private final UserMapper userMapper; // Mapper injection

    // VALİDASYON + KAYIT + DTO DÖNÜŞÜMÜ
    public UserResponseDTO registerUser(RegisterRequest request) {
        // Email zaten kayıtlı mı kontrol et
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        // RegisterRequest -> User (entity)
        User user = userMapper.toEntity(request);

        // Kaydet
        User savedUser = userRepository.save(user);

        // User (entity) -> UserResponseDTO
        return userMapper.toResponseDTO(savedUser);
        //burda(servis içinde) responseDTO dönüyoruz, controllerda ise bu dto'yu kullanıyoruz
    }

}

//[ Client / Frontend ]
//         |
//         | JSON isteği (RegisterRequest)
//         v
//[ Controller ]  (@PostMapping("/register"))
//         |
//         | registerRequest
//         v
//[ Service ]  (UserService)
//         |
//         | userMapper.toEntity(registerRequest)
//         v
//[ Mapper ]  (UserMapper)
//         |
//         | DTO → Entity dönüşümü
//         v
//[ Entity ]  (User)
//         |
//         | userRepository.save(user)
//         v
//[ Database ]

