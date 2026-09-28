package com.example.transit.service;

import com.example.transit.dto.PaymentRequest;
import com.example.transit.entity.Account;
import com.example.transit.entity.CustomerInvoice;
import com.example.transit.entity.Payment;
import com.example.transit.entity.VendorBill;
import com.example.transit.entity.enums.BillStatus;
import com.example.transit.entity.enums.InvoiceStatus;
import com.example.transit.entity.enums.JournalType;
import com.example.transit.entity.enums.PaymentType;
import com.example.transit.exception.InvalidOperationException;
import com.example.transit.exception.ResourceNotFoundException;
import com.example.transit.repository.CustomerInvoiceRepository;
import com.example.transit.repository.PaymentRepository;
import com.example.transit.repository.VendorBillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Registers fare collections from passengers (receipts) and payments to
 * vendors for fuel/maintenance bills, posting the matching cash/bank journal entries.
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerInvoiceRepository customerInvoiceRepository;
    private final VendorBillRepository vendorBillRepository;
    private final LedgerService ledgerService;

    @Transactional
    public Payment receiveTicketPayment(Long invoiceId, PaymentRequest request) {
        CustomerInvoice invoice = customerInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer invoice not found with id " + invoiceId));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new InvalidOperationException("Invoice " + invoice.getInvoiceNumber() + " is already paid");
        }

        Payment payment = Payment.builder()
                .paymentNumber("RCPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .paymentType(PaymentType.RECEIPT)
                .referenceType("INVOICE")
                .referenceId(invoice.getId())
                .amount(invoice.getAmount())
                .paymentDate(LocalDate.now())
                .mode(request.getMode())
                .build();
        payment = paymentRepository.save(payment);

        invoice.setStatus(InvoiceStatus.PAID);
        customerInvoiceRepository.save(invoice);

        Account cashOrBank = ledgerService.cashOrBank(request.getMode());
        Account receivable = ledgerService.resolveAccount(LedgerService.ACCOUNTS_RECEIVABLE);

        ledgerService.postDoubleEntry(
                cashOrBankJournalType(request),
                cashOrBank,
                receivable,
                payment.getAmount(),
                payment.getPaymentDate(),
                "Fare collection for invoice " + invoice.getInvoiceNumber(),
                "PAYMENT",
                payment.getId(),
                null
        );

        return payment;
    }

    @Transactional
    public Payment payVendorBill(Long billId, PaymentRequest request) {
        VendorBill bill = vendorBillRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor bill not found with id " + billId));

        if (bill.getStatus() == BillStatus.PAID) {
            throw new InvalidOperationException("Bill " + bill.getBillNumber() + " is already paid");
        }

        Payment payment = Payment.builder()
                .paymentNumber("PMT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .paymentType(PaymentType.PAYMENT)
                .referenceType("BILL")
                .referenceId(bill.getId())
                .amount(bill.getAmount())
                .paymentDate(LocalDate.now())
                .mode(request.getMode())
                .build();
        payment = paymentRepository.save(payment);

        bill.setStatus(BillStatus.PAID);
        vendorBillRepository.save(bill);

        Account cashOrBank = ledgerService.cashOrBank(request.getMode());
        Account payable = ledgerService.resolveAccount(LedgerService.ACCOUNTS_PAYABLE);

        ledgerService.postDoubleEntry(
                cashOrBankJournalType(request),
                payable,
                cashOrBank,
                payment.getAmount(),
                payment.getPaymentDate(),
                "Vendor payment for bill " + bill.getBillNumber(),
                "PAYMENT",
                payment.getId(),
                null
        );

        return payment;
    }

    private JournalType cashOrBankJournalType(PaymentRequest request) {
        return request.getMode() == com.example.transit.entity.enums.PaymentMode.CASH
                ? JournalType.CASH
                : JournalType.BANK;
    }

    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }
}
