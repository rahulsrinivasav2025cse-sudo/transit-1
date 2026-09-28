package com.example.transit.service;

import com.example.transit.dto.TicketBookingRequest;
import com.example.transit.entity.Bus;
import com.example.transit.entity.Contact;
import com.example.transit.entity.Route;
import com.example.transit.entity.SalesOrder;
import com.example.transit.entity.enums.OrderStatus;
import com.example.transit.exception.DoubleBookingException;
import com.example.transit.exception.InvalidOperationException;
import com.example.transit.exception.ResourceNotFoundException;
import com.example.transit.repository.SalesOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

/**
 * Handles passenger ticket booking (Sales Order): validates the bus/route/passenger,
 * prevents seat double-booking, and calculates a dynamic fare based on the route's
 * base fare and current occupancy of the bus for that trip (simple surge pricing).
 */
@Service
@RequiredArgsConstructor
public class SalesOrderService {

    private static final BigDecimal OCCUPANCY_SURGE_THRESHOLD = BigDecimal.valueOf(0.8); // 80% full
    private static final BigDecimal SURGE_MULTIPLIER = BigDecimal.valueOf(1.10); // +10%

    private final SalesOrderRepository salesOrderRepository;
    private final ContactService contactService;
    private final RouteService routeService;
    private final BusService busService;

    @Transactional
    public SalesOrder bookTicket(TicketBookingRequest request) {
        Contact passenger = contactService.getContactById(request.getPassengerId());
        Route route = routeService.getRouteById(request.getRouteId());
        Bus bus = busService.getBusById(request.getBusId());

        if (route.getBaseFare() == null) {
            throw new InvalidOperationException("Route " + route.getRouteCode() + " has no base fare configured");
        }

        // Prevent double-booking: same bus, same travel date, same seat, not cancelled
        salesOrderRepository.findByBus_IdAndTravelDateAndSeatNumberAndStatusNot(
                        bus.getId(), request.getTravelDate(), request.getSeatNumber(), OrderStatus.CANCELLED)
                .ifPresent(existing -> {
                    throw new DoubleBookingException(
                            "Seat " + request.getSeatNumber() + " on bus " + bus.getRegistrationNumber()
                                    + " is already booked for " + request.getTravelDate());
                });

        BigDecimal fare = calculateDynamicFare(route, bus, request.getTravelDate());

        SalesOrder order = SalesOrder.builder()
                .bookingRef("TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .passenger(passenger)
                .route(route)
                .bus(bus)
                .seatNumber(request.getSeatNumber())
                .travelDate(request.getTravelDate())
                .fare(fare)
                .status(OrderStatus.CONFIRMED)
                .build();

        return salesOrderRepository.save(order);
    }

    private BigDecimal calculateDynamicFare(Route route, Bus bus, java.time.LocalDate travelDate) {
        BigDecimal base = route.getBaseFare();

        long bookedSeats = salesOrderRepository.findByBus_IdAndTravelDate(bus.getId(), travelDate).stream()
                .filter(so -> so.getStatus() != OrderStatus.CANCELLED)
                .count();

        if (bus.getCapacity() == null || bus.getCapacity() == 0) {
            return base;
        }

        BigDecimal occupancy = BigDecimal.valueOf(bookedSeats)
                .divide(BigDecimal.valueOf(bus.getCapacity()), 4, RoundingMode.HALF_UP);

        if (occupancy.compareTo(OCCUPANCY_SURGE_THRESHOLD) >= 0) {
            return base.multiply(SURGE_MULTIPLIER).setScale(2, RoundingMode.HALF_UP);
        }
        return base.setScale(2, RoundingMode.HALF_UP);
    }

    public List<SalesOrder> getAll() {
        return salesOrderRepository.findAll();
    }

    public SalesOrder getById(Long id) {
        return salesOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found with id " + id));
    }

    @Transactional
    public SalesOrder cancel(Long id) {
        SalesOrder order = getById(id);
        order.setStatus(OrderStatus.CANCELLED);
        return salesOrderRepository.save(order);
    }
}
