package com.example.transit.service;

import com.example.transit.entity.AnalyticAccount;
import com.example.transit.entity.Route;
import com.example.transit.exception.ResourceNotFoundException;
import com.example.transit.repository.AnalyticAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticAccountService {

    private final AnalyticAccountRepository analyticAccountRepository;
    private final RouteService routeService;

    @Transactional
    public AnalyticAccount createAnalyticAccount(AnalyticAccount analyticAccount) {
        analyticAccount.setId(null);
        return analyticAccountRepository.save(analyticAccount);
    }

    public List<AnalyticAccount> getAll() {
        return analyticAccountRepository.findAll();
    }

    public AnalyticAccount getById(Long id) {
        return analyticAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analytic account not found with id " + id));
    }

    /**
     * Returns the analytic account tied to a route, auto-creating one
     * (named after the route) the first time a route is used in a transaction.
     */
    @Transactional
    public AnalyticAccount getOrCreateForRoute(Long routeId) {
        return analyticAccountRepository.findByRoute_Id(routeId)
                .orElseGet(() -> {
                    Route route = routeService.getRouteById(routeId);
                    AnalyticAccount aa = AnalyticAccount.builder()
                            .name(route.getRouteCode() + " - " + route.getRouteName())
                            .route(route)
                            .build();
                    return analyticAccountRepository.save(aa);
                });
    }
}
