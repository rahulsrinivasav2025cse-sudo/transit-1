package com.example.transit.repository;

import com.example.transit.entity.AnalyticAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalyticAccountRepository extends JpaRepository<AnalyticAccount, Long> {
    Optional<AnalyticAccount> findByRoute_Id(Long routeId);
}
