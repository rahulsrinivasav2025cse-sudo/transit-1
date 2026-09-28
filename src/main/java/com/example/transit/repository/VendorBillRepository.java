package com.example.transit.repository;

import com.example.transit.entity.VendorBill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendorBillRepository extends JpaRepository<VendorBill, Long> {
    Optional<VendorBill> findByPurchaseOrder_Id(Long purchaseOrderId);
}
