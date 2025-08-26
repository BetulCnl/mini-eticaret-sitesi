package com.betul.productmanagement.entity;

import com.betul.productmanagement.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_transaction_seq")
    @SequenceGenerator(name = "payment_transaction_seq", sequenceName = "payment_transaction_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private Long orderId; // Hangi siparişe ait olduğunu belirtir

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Instant createdAt;
}