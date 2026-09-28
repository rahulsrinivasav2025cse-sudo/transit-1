package com.example.transit.controller;

import com.example.transit.entity.Bus;
import com.example.transit.service.BusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
@RequiredArgsConstructor
public class BusController {

    private final BusService busService;

    @PostMapping
    public ResponseEntity<Bus> createBus(@Valid @RequestBody Bus bus) {
        return ResponseEntity.status(HttpStatus.CREATED).body(busService.createBus(bus));
    }

    @GetMapping
    public List<Bus> getAllBuses() {
        return busService.getAllBuses();
    }

    @GetMapping("/{id}")
    public Bus getBus(@PathVariable Long id) {
        return busService.getBusById(id);
    }

    @PutMapping("/{id}")
    public Bus updateBus(@PathVariable Long id, @Valid @RequestBody Bus bus) {
        return busService.updateBus(id, bus);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBus(@PathVariable Long id) {
        busService.deleteBus(id);
        return ResponseEntity.noContent().build();
    }
}
