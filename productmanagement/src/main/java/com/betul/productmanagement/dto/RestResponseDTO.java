package com.betul.productmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime date;  // Tarih eklendi
}


