package com.example.transit.repository;

import com.example.transit.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusRepository extends JpaRepository<Bus, Long> {
    boolean existsByRegistrationNumber(String registrationNumber);
}
