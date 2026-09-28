package com.example.transit.repository;

import com.example.transit.entity.CustomerInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerInvoiceRepository extends JpaRepository<CustomerInvoice, Long> {
    Optional<CustomerInvoice> findBySalesOrder_Id(Long salesOrderId);
}
