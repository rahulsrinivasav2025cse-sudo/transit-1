package com.example.transit.controller;

import com.example.transit.entity.Route;
import com.example.transit.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @PostMapping
    public ResponseEntity<Route> createRoute(@Valid @RequestBody Route route) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.createRoute(route));
    }

    @GetMapping
    public List<Route> getAllRoutes() {
        return routeService.getAllRoutes();
    }

    @GetMapping("/{id}")
    public Route getRoute(@PathVariable Long id) {
        return routeService.getRouteById(id);
    }

    @PutMapping("/{id}")
    public Route updateRoute(@PathVariable Long id, @Valid @RequestBody Route route) {
        return routeService.updateRoute(id, route);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }
}
