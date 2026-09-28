package com.example.transit.service;

import com.example.transit.entity.Route;
import com.example.transit.exception.InvalidOperationException;
import com.example.transit.exception.ResourceNotFoundException;
import com.example.transit.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;

    @Transactional
    public Route createRoute(Route route) {
        if (routeRepository.existsByRouteCode(route.getRouteCode())) {
            throw new InvalidOperationException("A route with code " + route.getRouteCode() + " already exists");
        }
        route.setId(null);
        return routeRepository.save(route);
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Route getRouteById(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id " + id));
    }

    @Transactional
    public Route updateRoute(Long id, Route updated) {
        Route route = getRouteById(id);
        route.setRouteName(updated.getRouteName());
        route.setStartPoint(updated.getStartPoint());
        route.setEndPoint(updated.getEndPoint());
        route.setDistanceKm(updated.getDistanceKm());
        route.setBaseFare(updated.getBaseFare());
        return routeRepository.save(route);
    }

    @Transactional
    public void deleteRoute(Long id) {
        routeRepository.delete(getRouteById(id));
    }
}
