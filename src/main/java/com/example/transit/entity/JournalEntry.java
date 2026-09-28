package com.example.transit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "journal_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_id", nullable = false)
    private Journal journal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analytic_account_id")
    private AnalyticAccount analyticAccount;

    @Builder.Default
    @Column(precision = 14, scale = 2)
    private BigDecimal debit = BigDecimal.ZERO;

    @Builder.Default
    @Column(precision = 14, scale = 2)
    private BigDecimal credit = BigDecimal.ZERO;

    private LocalDate entryDate;

    private String description;

    // e.g. "VENDOR_BILL", "CUSTOMER_INVOICE", "PAYMENT"
    private String referenceType;

    private Long referenceId;
}
