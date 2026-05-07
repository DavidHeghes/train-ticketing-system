package com.siemens.trainticketing.service;

import com.siemens.trainticketing.dto.RouteDTO;
import com.siemens.trainticketing.dto.StationResponse;
import com.siemens.trainticketing.entity.Station;
import com.siemens.trainticketing.entity.TrainSchedule;
import com.siemens.trainticketing.exception.RouteNotFoundException;
import com.siemens.trainticketing.exception.StationNotFoundException;
import com.siemens.trainticketing.repository.StationRepository;
import com.siemens.trainticketing.repository.TrainScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class RouteService {

    private final TrainScheduleRepository trainScheduleRepository;
    private final StationRepository stationRepository;

    @Autowired
    public RouteService(TrainScheduleRepository trainScheduleRepository,
                        StationRepository stationRepository) {
        this.trainScheduleRepository = trainScheduleRepository;
        this.stationRepository = stationRepository;
    }

    public List<RouteDTO> findRoutes(Long startStationId, Long endStationId) {
        if (!stationRepository.existsById(startStationId)) {
            throw new StationNotFoundException("Start station with ID " + startStationId + " not found");
        }
        if (!stationRepository.existsById(endStationId)) {
            throw new StationNotFoundException("End station with ID " + endStationId + " not found");
        }

        List<RouteDTO> validRoutes = new ArrayList<>();
        List<TrainSchedule> startDepartures = trainScheduleRepository.findByStationId(startStationId);
        List<TrainSchedule> endArrivals = trainScheduleRepository.findByStationId(endStationId);

        for (TrainSchedule start : startDepartures) {
            for (TrainSchedule end : endArrivals) {
                if (start.getTrain().getId().equals(end.getTrain().getId())) {
                    if (start.getStopOrder() < end.getStopOrder()) {
                        validRoutes.add(new RouteDTO(
                                "DIRECT",
                                start.getTrain().getName(),
                                start.getDepartureTime(),
                                null,
                                null,
                                null,
                                end.getArrivalTime()
                        ));
                    }
                } else {
                    List<TrainSchedule> route1 = trainScheduleRepository.findByTrainIdOrderByStopOrderAsc(start.getTrain().getId());
                    List<TrainSchedule> route2 = trainScheduleRepository.findByTrainIdOrderByStopOrderAsc(end.getTrain().getId());

                    for (TrainSchedule stop1 : route1) {
                        if (stop1.getStopOrder() > start.getStopOrder()) {
                            for (TrainSchedule stop2 : route2) {
                                if (stop2.getStopOrder() < end.getStopOrder() && stop1.getStation().getId().equals(stop2.getStation().getId())) {
                                    if (stop1.getArrivalTime() != null && stop2.getDepartureTime() != null && stop1.getArrivalTime().isBefore(stop2.getDepartureTime())) {
                                        validRoutes.add(new RouteDTO(
                                                "1-CHANGE",
                                                start.getTrain().getName(),
                                                start.getDepartureTime(),
                                                stop1.getStation().getName(),
                                                stop1.getArrivalTime(),
                                                end.getTrain().getName(),
                                                end.getArrivalTime()
                                        ));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (validRoutes.isEmpty()) {
            throw new RouteNotFoundException("No possible link found between the selected stations.");
        }

        return validRoutes;
    }

    public List<StationResponse> getAllStations() {
        List<StationResponse> responses = new ArrayList<>();
        for (Station station : stationRepository.findAll()) {
            responses.add(new StationResponse(station.getId(), station.getName()));
        }
        return responses;
    }
}