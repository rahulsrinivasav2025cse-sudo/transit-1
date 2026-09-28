package com.example.transit.service;

import com.example.transit.entity.Account;
import com.example.transit.entity.PurchaseOrder;
import com.example.transit.entity.VendorBill;
import com.example.transit.entity.enums.BillStatus;
import com.example.transit.entity.enums.JournalType;
import com.example.transit.exception.InvalidOperationException;
import com.example.transit.exception.ResourceNotFoundException;
import com.example.transit.repository.VendorBillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Converts a Purchase Order into a Vendor Bill (fuel purchase / fleet maintenance billing)
 * and posts the corresponding "Dr Expense / Cr Accounts Payable" journal entry.
 */
@Service
@RequiredArgsConstructor
public class VendorBillService {

    private final VendorBillRepository vendorBillRepository;
    private final PurchaseOrderService purchaseOrderService;
    private final LedgerService ledgerService;

    @Transactional
    public VendorBill convertToBill(Long purchaseOrderId) {
        PurchaseOrder po = purchaseOrderService.getById(purchaseOrderId);

        if (vendorBillRepository.findByPurchaseOrder_Id(purchaseOrderId).isPresent()) {
            throw new InvalidOperationException("A vendor bill already exists for purchase order " + po.getPoNumber());
        }

        VendorBill bill = VendorBill.builder()
                .billNumber("BILL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .purchaseOrder(po)
                .vendor(po.getVendor())
                .amount(po.getTotalAmount())
                .billDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(15))
                .status(BillStatus.UNPAID)
                .build();

        bill = vendorBillRepository.save(bill);

        Account expenseAccount = po.getProduct().getLinkedAccount() != null
                ? po.getProduct().getLinkedAccount()
                : ledgerService.resolveAccount(LedgerService.FUEL_MAINTENANCE_EXPENSE);
        Account payable = ledgerService.resolveAccount(LedgerService.ACCOUNTS_PAYABLE);

        ledgerService.postDoubleEntry(
                JournalType.PURCHASE,
                expenseAccount,
                payable,
                bill.getAmount(),
                bill.getBillDate(),
                "Vendor bill " + bill.getBillNumber() + " for PO " + po.getPoNumber(),
                "VENDOR_BILL",
                bill.getId(),
                null
        );

        return bill;
    }

    public List<VendorBill> getAll() {
        return vendorBillRepository.findAll();
    }

    public VendorBill getById(Long id) {
        return vendorBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor bill not found with id " + id));
    }
}
