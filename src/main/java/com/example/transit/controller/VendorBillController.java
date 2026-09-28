package com.example.transit.controller;

import com.example.transit.entity.VendorBill;
import com.example.transit.service.VendorBillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor-bills")
@RequiredArgsConstructor
public class VendorBillController {

    private final VendorBillService vendorBillService;

    // Converts a Purchase Order into a Vendor Bill: /api/vendor-bills/from-po/{purchaseOrderId}
    @PostMapping("/from-po/{purchaseOrderId}")
    public ResponseEntity<VendorBill> convertToBill(@PathVariable Long purchaseOrderId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vendorBillService.convertToBill(purchaseOrderId));
    }

    @GetMapping
    public List<VendorBill> getAll() {
        return vendorBillService.getAll();
    }

    @GetMapping("/{id}")
    public VendorBill getById(@PathVariable Long id) {
        return vendorBillService.getById(id);
    }
}
