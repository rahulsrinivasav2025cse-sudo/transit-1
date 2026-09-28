package com.example.transit.config;

import com.example.transit.entity.Account;
import com.example.transit.entity.Journal;
import com.example.transit.entity.Product;
import com.example.transit.entity.enums.AccountType;
import com.example.transit.entity.enums.JournalType;
import com.example.transit.entity.enums.ProductCategory;
import com.example.transit.entity.enums.ProductType;
import com.example.transit.repository.AccountRepository;
import com.example.transit.repository.JournalRepository;
import com.example.transit.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Seeds the standard Chart of Accounts, Journals and default Products on first startup,
 * so the transaction flows (Purchase Order -> Vendor Bill -> Payment, Ticket -> Invoice -> Payment)
 * have the control accounts they need. Runs only once - it checks for existing data first.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final JournalRepository journalRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void run(String... args) {
        seedAccounts();
        seedJournals();
        seedProducts();
    }

    private void seedAccounts() {
        seedAccount("1000", "Cash", AccountType.ASSET);
        seedAccount("1001", "Bank", AccountType.ASSET);
        seedAccount("1100", "Accounts Receivable", AccountType.ASSET);
        seedAccount("1200", "Bus Fleet Inventory", AccountType.ASSET);
        seedAccount("2000", "Vendor Creditors (Accounts Payable)", AccountType.LIABILITY);
        seedAccount("4000", "Fare Box Revenue", AccountType.INCOME);
        seedAccount("5000", "Fuel & Maintenance Expenses", AccountType.EXPENSE);
    }

    private void seedAccount(String code, String name, AccountType type) {
        if (accountRepository.findByAccountCode(code).isEmpty()) {
            accountRepository.save(Account.builder()
                    .accountCode(code)
                    .accountName(name)
                    .accountType(type)
                    .build());
        }
    }

    private void seedJournals() {
        seedJournal("Sales Journal", JournalType.SALES);
        seedJournal("Purchase Journal", JournalType.PURCHASE);
        seedJournal("Bank Journal", JournalType.BANK);
        seedJournal("Cash Journal", JournalType.CASH);
    }

    private void seedJournal(String name, JournalType type) {
        if (journalRepository.findFirstByType(type).isEmpty()) {
            journalRepository.save(Journal.builder().name(name).type(type).build());
        }
    }

    private void seedProducts() {
        Account revenue = accountRepository.findByAccountCode("4000").orElseThrow();
        Account expense = accountRepository.findByAccountCode("5000").orElseThrow();

        if (productRepository.findFirstByCategory(ProductCategory.TICKET).isEmpty()) {
            productRepository.save(Product.builder()
                    .name("Express Route Ticket")
                    .type(ProductType.SERVICE)
                    .category(ProductCategory.TICKET)
                    .unitPrice(BigDecimal.valueOf(50))
                    .linkedAccount(revenue)
                    .build());
        }
        if (productRepository.findFirstByCategory(ProductCategory.MAINTENANCE).isEmpty()) {
            productRepository.save(Product.builder()
                    .name("Fleet Maintenance")
                    .type(ProductType.SERVICE)
                    .category(ProductCategory.MAINTENANCE)
                    .unitPrice(BigDecimal.valueOf(5000))
                    .linkedAccount(expense)
                    .build());
        }
        if (productRepository.findFirstByCategory(ProductCategory.FUEL).isEmpty()) {
            productRepository.save(Product.builder()
                    .name("Diesel Fuel")
                    .type(ProductType.GOODS)
                    .category(ProductCategory.FUEL)
                    .unitPrice(BigDecimal.valueOf(1.2))
                    .linkedAccount(expense)
                    .build());
        }
    }
}
