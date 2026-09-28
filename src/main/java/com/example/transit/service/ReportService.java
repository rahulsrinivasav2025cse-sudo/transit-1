package com.example.transit.service;

import com.example.transit.dto.*;
import com.example.transit.entity.Budget;
import com.example.transit.entity.JournalEntry;
import com.example.transit.entity.Route;
import com.example.transit.entity.SalesOrder;
import com.example.transit.entity.enums.AccountType;
import com.example.transit.entity.enums.OrderStatus;
import com.example.transit.repository.BudgetRepository;
import com.example.transit.repository.JournalEntryRepository;
import com.example.transit.repository.SalesOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Produces the Balance Sheet, Profit & Loss, Budget Variance and Route Utilization reports
 * required by the Transit Authority, all derived from the posted journal entries.
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final JournalEntryRepository journalEntryRepository;
    private final BudgetRepository budgetRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final RouteService routeService;

    public BalanceSheetReport balanceSheet(LocalDate asOfDate) {
        LocalDate date = asOfDate != null ? asOfDate : LocalDate.now();

        Map<String, BigDecimal> assets = balanceByAccountType(AccountType.ASSET, date, true);
        Map<String, BigDecimal> liabilities = balanceByAccountType(AccountType.LIABILITY, date, false);

        BigDecimal totalAssets = sum(assets.values());
        BigDecimal totalLiabilities = sum(liabilities.values());

        return new BalanceSheetReport(date, assets, liabilities, totalAssets, totalLiabilities);
    }

    private Map<String, BigDecimal> balanceByAccountType(AccountType type, LocalDate asOfDate, boolean debitPositive) {
        List<JournalEntry> entries = journalEntryRepository
                .findByAccount_AccountTypeAndEntryDateLessThanEqual(type, asOfDate);

        Map<String, BigDecimal> result = new HashMap<>();
        for (JournalEntry entry : entries) {
            String accountName = entry.getAccount().getAccountName();
            BigDecimal delta = debitPositive
                    ? entry.getDebit().subtract(entry.getCredit())
                    : entry.getCredit().subtract(entry.getDebit());
            result.merge(accountName, delta, BigDecimal::add);
        }
        return result;
    }

    public ProfitAndLossReport profitAndLoss(LocalDate from, LocalDate to) {
        LocalDate start = from != null ? from : LocalDate.now().withDayOfMonth(1);
        LocalDate end = to != null ? to : LocalDate.now();

        List<JournalEntry> incomeEntries = journalEntryRepository
                .findByAccount_AccountTypeAndEntryDateBetween(AccountType.INCOME, start, end);
        List<JournalEntry> expenseEntries = journalEntryRepository
                .findByAccount_AccountTypeAndEntryDateBetween(AccountType.EXPENSE, start, end);

        Map<String, BigDecimal> incomeByAccount = new HashMap<>();
        for (JournalEntry e : incomeEntries) {
            incomeByAccount.merge(e.getAccount().getAccountName(), e.getCredit().subtract(e.getDebit()), BigDecimal::add);
        }

        Map<String, BigDecimal> expenseByAccount = new HashMap<>();
        for (JournalEntry e : expenseEntries) {
            expenseByAccount.merge(e.getAccount().getAccountName(), e.getDebit().subtract(e.getCredit()), BigDecimal::add);
        }

        BigDecimal totalIncome = sum(incomeByAccount.values());
        BigDecimal totalExpense = sum(expenseByAccount.values());

        return new ProfitAndLossReport(start, end, incomeByAccount, expenseByAccount,
                totalIncome, totalExpense, totalIncome.subtract(totalExpense));
    }

    public List<BudgetReportItem> budgetReport() {
        List<Budget> budgets = budgetRepository.findAll();
        return budgets.stream().map(budget -> {
            List<JournalEntry> entries = journalEntryRepository.findByAnalyticAccount_IdAndEntryDateBetween(
                    budget.getAnalyticAccount().getId(), budget.getPeriodStart(), budget.getPeriodEnd());

            BigDecimal actual = entries.stream()
                    .map(e -> e.getCredit().subtract(e.getDebit()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal variance = actual.subtract(budget.getPlannedAmount());

            return new BudgetReportItem(
                    budget.getId(),
                    budget.getName(),
                    budget.getAnalyticAccount().getName(),
                    budget.getPlannedAmount(),
                    actual,
                    variance,
                    budget.getPeriodStart(),
                    budget.getPeriodEnd()
            );
        }).collect(Collectors.toList());
    }

    public RouteUtilizationReport routeUtilization(Long routeId, LocalDate from, LocalDate to) {
        Route route = routeService.getRouteById(routeId);
        LocalDate start = from != null ? from : LocalDate.now().withDayOfMonth(1);
        LocalDate end = to != null ? to : LocalDate.now();

        List<SalesOrder> orders = salesOrderRepository.findByRoute_IdAndTravelDateBetween(routeId, start, end)
                .stream()
                .filter(so -> so.getStatus() != OrderStatus.CANCELLED)
                .collect(Collectors.toList());

        long totalBookings = orders.size();
        BigDecimal totalRevenue = orders.stream().map(SalesOrder::getFare).reduce(BigDecimal.ZERO, BigDecimal::add);

        // capacity offered = sum of capacity for each distinct (bus, travelDate) trip actually used
        long totalCapacityOffered = orders.stream()
                .map(so -> so.getBus().getId() + "_" + so.getTravelDate())
                .distinct()
                .mapToLong(key -> {
                    Long busId = Long.valueOf(key.split("_")[0]);
                    return orders.stream()
                            .filter(so -> so.getBus().getId().equals(busId))
                            .findFirst()
                            .map(so -> so.getBus().getCapacity() != null ? so.getBus().getCapacity().longValue() : 0L)
                            .orElse(0L);
                })
                .sum();

        double occupancy = totalCapacityOffered > 0
                ? (totalBookings * 100.0) / totalCapacityOffered
                : 0.0;

        return new RouteUtilizationReport(route.getId(), route.getRouteName(), start, end,
                totalBookings, totalRevenue, totalCapacityOffered, occupancy);
    }

    private BigDecimal sum(java.util.Collection<BigDecimal> values) {
        return values.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
