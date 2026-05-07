package com.siemens.trainticketing.dto;

public record TrainRequest(
        String name,
        int totalSeats
) {
}