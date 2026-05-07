package com.siemens.trainticketing.dto;

public record BookingRequest(
        Long trainId,
        String customerEmail,
        int numberOfTickets
) {}