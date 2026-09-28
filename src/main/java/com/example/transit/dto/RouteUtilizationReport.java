package com.example.transit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteUtilizationReport {
    private Long routeId;
    private String routeName;
    private LocalDate from;
    private LocalDate to;
    private long totalBookings;
    private BigDecimal totalRevenue;
    private long totalCapacityOffered;
    private double occupancyRatePercent;
}
