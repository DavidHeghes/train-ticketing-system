package com.siemens.trainticketing.dto;

public record BookingResponse(
        Long id,
        String trainName,
        String customerEmail,
        int numberOfTickets
) {
}