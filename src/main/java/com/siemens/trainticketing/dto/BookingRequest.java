package com.siemens.trainticketing.dto;

public record BookingRequest(
        Long trainId,
        Long startStationId,
        Long endStationId,
        String customerEmail,
        int numberOfTickets
) {}