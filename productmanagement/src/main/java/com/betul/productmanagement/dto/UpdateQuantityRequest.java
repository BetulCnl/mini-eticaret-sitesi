package com.betul.productmanagement.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateQuantityRequest {
    @Min(1)
    private int quantity;
} 