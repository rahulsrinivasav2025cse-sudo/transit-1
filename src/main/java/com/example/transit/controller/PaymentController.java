package com.example.transit.controller;

import com.example.transit.dto.PaymentRequest;
import com.example.transit.entity.Payment;
import com.example.transit.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Register fare collection from a passenger for an unpaid invoice
    @PostMapping("/receive/{invoiceId}")
    public ResponseEntity<Payment> receiveTicketPayment(@PathVariable Long invoiceId,
                                                          @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.receiveTicketPayment(invoiceId, request));
    }

    // Pay a vendor bill (fuel purchase / maintenance) via Cash or Bank
    @PostMapping("/pay/{billId}")
    public ResponseEntity<Payment> payVendorBill(@PathVariable Long billId,
                                                   @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.payVendorBill(billId, request));
    }

    @GetMapping
    public List<Payment> getAll() {
        return paymentService.getAll();
    }
}
