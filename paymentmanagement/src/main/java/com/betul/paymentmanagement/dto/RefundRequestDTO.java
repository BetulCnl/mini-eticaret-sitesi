package com.betul.paymentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class RefundRequestDTO {
        private Long orderId;
        private BigDecimal amount;
    }

