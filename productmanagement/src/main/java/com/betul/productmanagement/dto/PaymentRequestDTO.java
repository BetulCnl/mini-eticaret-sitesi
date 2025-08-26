package com.betul.productmanagement.dto;

import com.betul.productmanagement.enums.PaymentStatus;
import com.betul.productmanagement.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    private Long orderId;
    private BigDecimal amount;
    private PaymentType paymentType;
    private PaymentStatus paymentStatus;
}
