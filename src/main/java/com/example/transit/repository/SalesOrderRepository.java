package com.example.transit.repository;

import com.example.transit.entity.SalesOrder;
import com.example.transit.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {

    Optional<SalesOrder> findByBus_IdAndTravelDateAndSeatNumberAndStatusNot(
            Long busId, LocalDate travelDate, String seatNumber, OrderStatus excludedStatus);

    List<SalesOrder> findByRoute_IdAndTravelDateBetween(Long routeId, LocalDate from, LocalDate to);

    List<SalesOrder> findByBus_IdAndTravelDate(Long busId, LocalDate travelDate);
}
