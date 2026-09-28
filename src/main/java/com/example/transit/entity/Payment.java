package com.example.transit.entity;

import com.example.transit.entity.enums.PaymentMode;
import com.example.transit.entity.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String paymentNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType; // RECEIPT (from passenger) or PAYMENT (to vendor)

    // "INVOICE" or "BILL"
    private String referenceType;

    private Long referenceId;

    @Column(precision = 14, scale = 2)
    private BigDecimal amount;

    @Builder.Default
    private LocalDate paymentDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMode mode;
}
