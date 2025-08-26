package com.betul.productmanagement.dto;

import com.betul.productmanagement.enums.PaymentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentResponseDTO {
    private Long paymentTransactionId;
    private PaymentStatus status;
    private String message;
}
