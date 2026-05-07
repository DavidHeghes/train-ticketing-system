package com.siemens.trainticketing.dto;

import java.time.LocalTime;

public record TrainScheduleRequest(
        Long trainId,
        Long stationId,
        int stopOrder,
        LocalTime arrivalTime,
        LocalTime departureTime
) {
}