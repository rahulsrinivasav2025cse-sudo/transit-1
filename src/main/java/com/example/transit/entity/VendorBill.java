package com.example.transit.entity;

import com.example.transit.entity.enums.BillStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "vendor_bills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String billNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Contact vendor;

    @Column(precision = 14, scale = 2)
    private BigDecimal amount;

    @Builder.Default
    private LocalDate billDate = LocalDate.now();

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BillStatus status = BillStatus.UNPAID;
}
