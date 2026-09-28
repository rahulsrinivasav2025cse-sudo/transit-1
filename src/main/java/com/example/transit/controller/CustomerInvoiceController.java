package com.example.transit.controller;

import com.example.transit.entity.CustomerInvoice;
import com.example.transit.service.CustomerInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class CustomerInvoiceController {

    private final CustomerInvoiceService customerInvoiceService;

    // Generates a passenger's digital receipt from a confirmed booking: /api/invoices/from-ticket/{salesOrderId}
    @PostMapping("/from-ticket/{salesOrderId}")
    public ResponseEntity<CustomerInvoice> generateInvoice(@PathVariable Long salesOrderId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerInvoiceService.generateInvoice(salesOrderId));
    }

    @GetMapping
    public List<CustomerInvoice> getAll() {
        return customerInvoiceService.getAll();
    }

    @GetMapping("/{id}")
    public CustomerInvoice getById(@PathVariable Long id) {
        return customerInvoiceService.getById(id);
    }
}
