package com.siemens.trainticketing.dto;

public record TrainResponse(
        Long id,
        String name,
        int totalSeats
) {
}