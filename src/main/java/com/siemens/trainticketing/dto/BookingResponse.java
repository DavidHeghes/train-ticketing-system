package com.siemens.trainticketing.dto;

import java.time.LocalTime;

public record BookingResponse(
        Long id,
        String trainName,
        String startStationName,
        String endStationName,
        LocalTime departureTime,
        LocalTime arrivalTime,
        int numberOfTickets,
        String customerEmail
) {}