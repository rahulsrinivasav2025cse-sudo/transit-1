package com.example.transit.controller;

import com.example.transit.dto.TicketBookingRequest;
import com.example.transit.entity.SalesOrder;
import com.example.transit.service.SalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Passenger ticket booking. POST /api/tickets creates a Sales Order with
 * automatic seat double-booking prevention and dynamic fare calculation.
 */
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final SalesOrderService salesOrderService;

    @PostMapping
    public ResponseEntity<SalesOrder> bookTicket(@Valid @RequestBody TicketBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salesOrderService.bookTicket(request));
    }

    @GetMapping
    public List<SalesOrder> getAllTickets() {
        return salesOrderService.getAll();
    }

    @GetMapping("/{id}")
    public SalesOrder getTicket(@PathVariable Long id) {
        return salesOrderService.getById(id);
    }

    @PostMapping("/{id}/cancel")
    public SalesOrder cancelTicket(@PathVariable Long id) {
        return salesOrderService.cancel(id);
    }
}
