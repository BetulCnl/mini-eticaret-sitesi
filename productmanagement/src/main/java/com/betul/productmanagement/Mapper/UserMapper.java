package com.betul.productmanagement.Mapper;

import com.betul.productmanagement.dto.RegisterRequest;
import com.betul.productmanagement.dto.UserResponseDTO;
import com.betul.productmanagement.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import com.betul.productmanagement.enums.Role;

@Component
public class UserMapper { // büyük harfle

    private final BCryptPasswordEncoder passwordEncoder;

    public UserMapper(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User toEntity(RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        return user;
    }
    public UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        return dto;
    }
}

