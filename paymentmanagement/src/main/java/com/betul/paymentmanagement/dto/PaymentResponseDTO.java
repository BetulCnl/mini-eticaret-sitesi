package com.betul.paymentmanagement.dto;

// bu PaymentResponseDTO ödeme işlemi tamamlandıktan sonra productmanagement servisine geri döneceğimiz cevapları bulunduruyor

import com.betul.paymentmanagement.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private Long paymentTransactionId;
    private PaymentStatus status;
    private String message;
}
