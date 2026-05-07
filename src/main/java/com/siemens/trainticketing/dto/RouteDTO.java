package com.siemens.trainticketing.dto;

import java.time.LocalTime;

public record RouteDTO(
        String routeType,
        String firstTrainName,
        LocalTime departureTime,
        String transferStation,
        LocalTime transferTime,
        String secondTrainName,
        LocalTime finalArrivalTime
) {
}