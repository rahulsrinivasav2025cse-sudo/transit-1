package com.example.transit.controller;

import com.example.transit.dto.BalanceSheetReport;
import com.example.transit.dto.BudgetReportItem;
import com.example.transit.dto.ProfitAndLossReport;
import com.example.transit.dto.RouteUtilizationReport;
import com.example.transit.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/balance-sheet")
    public BalanceSheetReport balanceSheet(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        return reportService.balanceSheet(asOfDate);
    }

    @GetMapping("/profit-and-loss")
    public ProfitAndLossReport profitAndLoss(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reportService.profitAndLoss(from, to);
    }

    @GetMapping("/budget-variance")
    public List<BudgetReportItem> budgetReport() {
        return reportService.budgetReport();
    }

    @GetMapping("/route-utilization/{routeId}")
    public RouteUtilizationReport routeUtilization(
            @PathVariable Long routeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reportService.routeUtilization(routeId, from, to);
    }
}
