package com.betul.productmanagement.service;

import com.betul.productmanagement.dto.PaymentRequestDTO;
import com.betul.productmanagement.entity.PaymentTransaction;
import com.betul.productmanagement.enums.PaymentStatus;
import com.betul.productmanagement.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    /**
     * Sipariş iptali veya refund işlemlerinde,
     * ana OrderService transactionından bağımsız olarak kaydetmek için.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveTransactionInNewTx(Long orderId, BigDecimal amount, PaymentStatus status) {
        PaymentTransaction tx = new PaymentTransaction();
        tx.setOrderId(orderId);
        tx.setAmount(amount);
        tx.setStatus(status);
        tx.setCreatedAt(Instant.now());
        repository.save(tx);
    }

    // Burada diğer PaymentTransactionService metodları da olabilir,
    // mevcut yapıyı bozacak herhangi bir değişiklik yapılmadı.
}
