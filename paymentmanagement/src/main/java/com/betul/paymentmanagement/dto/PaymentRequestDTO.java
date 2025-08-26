package com.betul.paymentmanagement.dto;

// Bu PaymentRequestDTO productmanagement servisinden bir ödeme isteği alırken
//  hangi bilgileri beklediğimizi tanımlayacak.

import com.betul.paymentmanagement.enums.PaymentStatus;
import com.betul.paymentmanagement.enums.PaymentType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentRequestDTO {
    private Long orderId;
    private BigDecimal amount;
    private PaymentType paymentType;
    private PaymentStatus paymentStatus;
}

// productmanagement servisi, paymentmanagement servisinin sadece ve sadece API "sözleşmesini" bilmelidir.
// Bu sözleşme de bizim oluşturduğumuz DTO'lardır (PaymentRequestDTO,PaymentResponseDTO)
// yani productmanagement servisi paymentmanagement servisinin iç yapısını ya da işte entitysini bilmemelidir.