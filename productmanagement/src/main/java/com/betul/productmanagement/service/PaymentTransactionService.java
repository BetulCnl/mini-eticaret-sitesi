package com.betul.productmanagement.service;

import com.betul.productmanagement.dto.PaymentRequestDTO;
import com.betul.productmanagement.dto.PaymentResponseDTO;
import com.betul.productmanagement.entity.PaymentTransaction;
import com.betul.productmanagement.enums.PaymentStatus;
import com.betul.productmanagement.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PaymentTransactionService {

    private final PaymentTransactionRepository repository;

    /**
     * Eğer ProductManagement içinden direk test veya HTML form ile işlem yapıyorsak,
     * processPayment metodunu kullanabiliriz (isteğe bağlı).
     */
    public PaymentTransaction initiatePayment(PaymentRequestDTO request) {
        // Örnek: PENDING transaction oluştur ve kaydet
        PaymentTransaction tx = PaymentTransaction.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .status(PaymentStatus.PENDING)
                .createdAt(Instant.now())
                .build();

        return repository.save(tx);
    }
}

