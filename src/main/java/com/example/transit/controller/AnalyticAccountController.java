package com.example.transit.controller;

import com.example.transit.entity.AnalyticAccount;
import com.example.transit.service.AnalyticAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytic-accounts")
@RequiredArgsConstructor
public class AnalyticAccountController {

    private final AnalyticAccountService analyticAccountService;

    @PostMapping
    public ResponseEntity<AnalyticAccount> create(@Valid @RequestBody AnalyticAccount analyticAccount) {
        return ResponseEntity.status(HttpStatus.CREATED).body(analyticAccountService.createAnalyticAccount(analyticAccount));
    }

    @GetMapping
    public List<AnalyticAccount> getAll() {
        return analyticAccountService.getAll();
    }

    @GetMapping("/{id}")
    public AnalyticAccount getById(@PathVariable Long id) {
        return analyticAccountService.getById(id);
    }
}
