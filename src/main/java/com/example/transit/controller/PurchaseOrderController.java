package com.example.transit.controller;

import com.example.transit.dto.PurchaseOrderRequest;
import com.example.transit.entity.PurchaseOrder;
import com.example.transit.service.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<PurchaseOrder> createPurchaseOrder(@Valid @RequestBody PurchaseOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseOrderService.createPurchaseOrder(request));
    }

    @GetMapping
    public List<PurchaseOrder> getAll() {
        return purchaseOrderService.getAll();
    }

    @GetMapping("/{id}")
    public PurchaseOrder getById(@PathVariable Long id) {
        return purchaseOrderService.getById(id);
    }
}
