package com.example.transit.controller;

import com.example.transit.dto.BudgetRequest;
import com.example.transit.entity.Budget;
import com.example.transit.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<Budget> createBudget(@Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.createBudget(request));
    }

    @GetMapping
    public List<Budget> getAll() {
        return budgetService.getAll();
    }

    @GetMapping("/{id}")
    public Budget getById(@PathVariable Long id) {
        return budgetService.getById(id);
    }
}
