package com.betul.paymentmanagement.controller;

import com.betul.paymentmanagement.dto.PaymentRequestDTO;
import com.betul.paymentmanagement.dto.PaymentResponseDTO;
import com.betul.paymentmanagement.service.PaymentProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentProcessingService paymentService;

    public PaymentController(PaymentProcessingService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(@RequestBody PaymentRequestDTO request) {
        PaymentResponseDTO response = paymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }
}
