package com.betul.paymentmanagement.service;

import com.betul.paymentmanagement.dto.PaymentRequestDTO;
import com.betul.paymentmanagement.dto.PaymentResponseDTO;
import com.betul.paymentmanagement.enums.PaymentStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentProcessingService {

    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        System.out.println("Processing payment for order: " + request.getOrderId());

        // Simulate processing time
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Determine the final status from the request
        PaymentStatus finalStatus = request.getPaymentStatus() != null ? request.getPaymentStatus() : PaymentStatus.FAILED;

        System.out.println("Payment for order " + request.getOrderId() + " processed with status: " + finalStatus);

        // Generate a random transaction ID for the response, since we have no database
        long randomTransactionId = Math.abs(UUID.randomUUID().getMostSignificantBits());

        // Create and return the response DTO
        return new PaymentResponseDTO(randomTransactionId, finalStatus, "Payment processed by stateless service.");
    }
}
