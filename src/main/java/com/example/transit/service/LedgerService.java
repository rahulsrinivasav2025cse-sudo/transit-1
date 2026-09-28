package com.example.transit.service;

import com.example.transit.entity.Account;
import com.example.transit.entity.AnalyticAccount;
import com.example.transit.entity.Journal;
import com.example.transit.entity.JournalEntry;
import com.example.transit.entity.enums.JournalType;
import com.example.transit.exception.InvalidOperationException;
import com.example.transit.repository.AccountRepository;
import com.example.transit.repository.JournalEntryRepository;
import com.example.transit.repository.JournalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Central helper that posts balanced double-entry journal entries
 * (one debit line + one credit line) for every financial transaction
 * in the system, and resolves the standard Chart-of-Accounts control
 * accounts (Cash, Bank, Accounts Receivable, Accounts Payable, etc.)
 * by their seeded account codes.
 */
@Service
@RequiredArgsConstructor
public class LedgerService {

    public static final String CASH = "1000";
    public static final String BANK = "1001";
    public static final String ACCOUNTS_RECEIVABLE = "1100";
    public static final String ACCOUNTS_PAYABLE = "2000";
    public static final String FARE_BOX_REVENUE = "4000";
    public static final String FUEL_MAINTENANCE_EXPENSE = "5000";

    private final AccountRepository accountRepository;
    private final JournalRepository journalRepository;
    private final JournalEntryRepository journalEntryRepository;

    public Account resolveAccount(String code) {
        return accountRepository.findByAccountCode(code)
                .orElseThrow(() -> new InvalidOperationException(
                        "Required Chart-of-Accounts entry with code " + code + " is missing. " +
                                "It should have been created by the startup data seeder."));
    }

    public Journal resolveJournal(JournalType type) {
        return journalRepository.findFirstByType(type)
                .orElseThrow(() -> new InvalidOperationException(
                        "Required journal of type " + type + " is missing. " +
                                "It should have been created by the startup data seeder."));
    }

    /**
     * Posts a balanced pair of journal entries: Debit drAccount / Credit crAccount, both for `amount`.
     */
    @Transactional
    public void postDoubleEntry(JournalType journalType, Account drAccount, Account crAccount,
                                 BigDecimal amount, LocalDate date, String description,
                                 String referenceType, Long referenceId, AnalyticAccount analyticAccount) {

        Journal journal = resolveJournal(journalType);

        JournalEntry debitLine = JournalEntry.builder()
                .journal(journal)
                .account(drAccount)
                .analyticAccount(analyticAccount)
                .debit(amount)
                .credit(BigDecimal.ZERO)
                .entryDate(date)
                .description(description)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .build();

        JournalEntry creditLine = JournalEntry.builder()
                .journal(journal)
                .account(crAccount)
                .analyticAccount(analyticAccount)
                .debit(BigDecimal.ZERO)
                .credit(amount)
                .entryDate(date)
                .description(description)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .build();

        journalEntryRepository.save(debitLine);
        journalEntryRepository.save(creditLine);
    }

    public Account cashOrBank(com.example.transit.entity.enums.PaymentMode mode) {
        // DIGITAL payments are settled through the bank control account
        if (mode == com.example.transit.entity.enums.PaymentMode.CASH) {
            return resolveAccount(CASH);
        }
        return resolveAccount(BANK);
    }
}
