package com.betul.paymentmanagement.controller;

import com.betul.paymentmanagement.dto.PaymentRequestDTO;
import com.betul.paymentmanagement.dto.PaymentResponseDTO;
import com.betul.paymentmanagement.dto.RefundRequestDTO;
import com.betul.paymentmanagement.dto.RefundResponseDTO;
import com.betul.paymentmanagement.service.PaymentProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentProcessingService paymentService;

    public PaymentController(PaymentProcessingService paymentService) {
        this.paymentService = paymentService;
    }

    // Mevcut purchase endpoint
    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(@RequestBody PaymentRequestDTO request) {
        PaymentResponseDTO response = paymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }

    // Yeni refund endpoint (aynı controller)
    @PostMapping("/refund")
    public ResponseEntity<RefundResponseDTO> refundPayment(@RequestBody RefundRequestDTO request) {
        boolean approved = new Random().nextBoolean();

        RefundResponseDTO response = new RefundResponseDTO(
                request.getOrderId(),
                approved ? "SUCCESS_CLAIM" : "FAILED_CLAIM",
                approved ? "Refund approved" : "Refund declined"
        );

        return ResponseEntity.ok(response);
    }
}

