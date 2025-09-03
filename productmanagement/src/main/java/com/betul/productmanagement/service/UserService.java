package com.betul.productmanagement.service;

import com.betul.productmanagement.dto.RegisterRequest;
import com.betul.productmanagement.dto.UserResponseDTO;
import com.betul.productmanagement.entity.User;
import com.betul.productmanagement.repository.UserRepository;
import com.betul.productmanagement.Mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.betul.productmanagement.enums.Role; // Doğru paket ve enum adı

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

//bu service UserService: kullanıcı verisi oluşturma ve yönetme işlemi yapar.
//kullanıcı kayıt işlemleri, kullanıcı bilgilerini bulma ve DTO dönüşümleri gibi işlemleri içerir.

    private final UserRepository userRepository;
    private final UserMapper userMapper; // Mapper injection
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // VALİDASYON + KAYIT + DTO DÖNÜŞÜMÜ
    public UserResponseDTO registerUser(RegisterRequest request) {
        logger.info("Registering user with request: {}", request);

        // Email zaten kayıtlı mı kontrol et
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            logger.warn("Registration failed: Email already in use: {}", request.getEmail());
            throw new RuntimeException("Email already in use");
        }
        logger.info("Email {} is not in use. Proceeding with registration.", request.getEmail());

        // RegisterRequest -> User (entity)
        User user = userMapper.toEntity(request);
        // Frontend'den rol gelmiyorsa veya null ise varsayılan olarak USER ata
        user.setRole(request.getRole() != null ? request.getRole() : Role.USER);
        logger.info("Mapped RegisterRequest to User entity: {}", user);

        // Kaydet
        User savedUser = userRepository.save(user);
        logger.info("User saved successfully: {}", savedUser);

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

