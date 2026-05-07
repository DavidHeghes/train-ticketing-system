package com.siemens.trainticketing.dto;

import java.time.LocalTime;

public record TrainScheduleResponse(
        Long id,
        String trainName,
        String stationName,
        int stopOrder,
        LocalTime arrivalTime,
        LocalTime departureTime
) {
}