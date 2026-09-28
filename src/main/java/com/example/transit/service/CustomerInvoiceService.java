package com.example.transit.service;

import com.example.transit.entity.Account;
import com.example.transit.entity.AnalyticAccount;
import com.example.transit.entity.CustomerInvoice;
import com.example.transit.entity.SalesOrder;
import com.example.transit.entity.enums.InvoiceStatus;
import com.example.transit.entity.enums.JournalType;
import com.example.transit.exception.InvalidOperationException;
import com.example.transit.exception.ResourceNotFoundException;
import com.example.transit.repository.CustomerInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Generates the passenger's digital booking receipt (Customer Invoice) for a confirmed
 * ticket Sales Order and posts "Dr Accounts Receivable / Cr Fare Box Revenue".
 */
@Service
@RequiredArgsConstructor
public class CustomerInvoiceService {

    private final CustomerInvoiceRepository customerInvoiceRepository;
    private final SalesOrderService salesOrderService;
    private final LedgerService ledgerService;
    private final AnalyticAccountService analyticAccountService;

    @Transactional
    public CustomerInvoice generateInvoice(Long salesOrderId) {
        SalesOrder salesOrder = salesOrderService.getById(salesOrderId);

        if (customerInvoiceRepository.findBySalesOrder_Id(salesOrderId).isPresent()) {
            throw new InvalidOperationException(
                    "An invoice already exists for booking " + salesOrder.getBookingRef());
        }

        CustomerInvoice invoice = CustomerInvoice.builder()
                .invoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .salesOrder(salesOrder)
                .passenger(salesOrder.getPassenger())
                .amount(salesOrder.getFare())
                .invoiceDate(LocalDate.now())
                .status(InvoiceStatus.UNPAID)
                .build();

        invoice = customerInvoiceRepository.save(invoice);

        Account receivable = ledgerService.resolveAccount(LedgerService.ACCOUNTS_RECEIVABLE);
        Account revenue = ledgerService.resolveAccount(LedgerService.FARE_BOX_REVENUE);
        AnalyticAccount analyticAccount = analyticAccountService.getOrCreateForRoute(salesOrder.getRoute().getId());

        ledgerService.postDoubleEntry(
                JournalType.SALES,
                receivable,
                revenue,
                invoice.getAmount(),
                invoice.getInvoiceDate(),
                "Ticket invoice " + invoice.getInvoiceNumber() + " for booking " + salesOrder.getBookingRef(),
                "CUSTOMER_INVOICE",
                invoice.getId(),
                analyticAccount
        );

        return invoice;
    }

    public List<CustomerInvoice> getAll() {
        return customerInvoiceRepository.findAll();
    }

    public CustomerInvoice getById(Long id) {
        return customerInvoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer invoice not found with id " + id));
    }
}
