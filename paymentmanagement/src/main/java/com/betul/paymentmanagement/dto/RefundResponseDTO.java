package com.betul.paymentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class RefundResponseDTO {
        private Long orderId;
        private String claimStatus; // SUCCESS_CLAIM / FAILED_CLAIM
        private String message;
    }


