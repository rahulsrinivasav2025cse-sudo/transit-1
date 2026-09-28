package com.example.transit.repository;

import com.example.transit.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {
    boolean existsByRouteCode(String routeCode);
}
