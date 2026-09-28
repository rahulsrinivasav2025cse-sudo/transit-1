package com.example.transit.service;

import com.example.transit.dto.BudgetRequest;
import com.example.transit.entity.AnalyticAccount;
import com.example.transit.entity.Budget;
import com.example.transit.exception.ResourceNotFoundException;
import com.example.transit.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final AnalyticAccountService analyticAccountService;

    @Transactional
    public Budget createBudget(BudgetRequest request) {
        AnalyticAccount analyticAccount = analyticAccountService.getById(request.getAnalyticAccountId());

        Budget budget = Budget.builder()
                .name(request.getName())
                .analyticAccount(analyticAccount)
                .plannedAmount(request.getPlannedAmount())
                .periodStart(request.getPeriodStart())
                .periodEnd(request.getPeriodEnd())
                .build();

        return budgetRepository.save(budget);
    }

    public List<Budget> getAll() {
        return budgetRepository.findAll();
    }

    public Budget getById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id " + id));
    }
}
