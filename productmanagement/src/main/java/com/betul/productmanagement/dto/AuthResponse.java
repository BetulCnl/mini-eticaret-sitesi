package com.betul.productmanagement.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String message;
    private String name;
    private String email;

    public AuthResponse(String token, String message, String name, String email) {
        this.token = token;
        this.message = message;
        this.name = name;
        this.email = email;
    }

    public AuthResponse(String message) { // hata durumları için
        this.token = null;
        this.message = message;
        this.name = null;
        this.email = null;
    }
}