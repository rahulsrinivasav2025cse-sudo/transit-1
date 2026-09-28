package com.example.transit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "budgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analytic_account_id", nullable = false)
    private AnalyticAccount analyticAccount;

    @Column(precision = 14, scale = 2)
    private BigDecimal plannedAmount;

    private LocalDate periodStart;

    private LocalDate periodEnd;
}
