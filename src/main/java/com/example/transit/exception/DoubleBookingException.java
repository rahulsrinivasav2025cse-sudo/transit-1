package com.example.transit.exception;

public class DoubleBookingException extends RuntimeException {
    public DoubleBookingException(String message) {
        super(message);
    }
}
