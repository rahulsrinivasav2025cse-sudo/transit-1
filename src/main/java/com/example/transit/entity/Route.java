package com.example.transit.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String routeCode;

    @NotBlank
    private String routeName;

    private String startPoint;

    private String endPoint;

    private Double distanceKm;

    @Column(precision = 12, scale = 2)
    private BigDecimal baseFare;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
