package com.example.transit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TicketBookingRequest {

    @NotNull
    private Long passengerId;

    @NotNull
    private Long routeId;

    @NotNull
    private Long busId;

    @NotBlank
    private String seatNumber;

    @NotNull
    private LocalDate travelDate;
}
