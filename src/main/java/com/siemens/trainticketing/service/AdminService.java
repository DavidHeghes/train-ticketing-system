package com.siemens.trainticketing.service;

import com.siemens.trainticketing.dto.*;
import com.siemens.trainticketing.entity.Booking;
import com.siemens.trainticketing.entity.Station;
import com.siemens.trainticketing.entity.Train;
import com.siemens.trainticketing.entity.TrainSchedule;
import com.siemens.trainticketing.exception.StationNotFoundException;
import com.siemens.trainticketing.exception.TrainNotFoundException;
import com.siemens.trainticketing.repository.BookingRepository;
import com.siemens.trainticketing.repository.StationRepository;
import com.siemens.trainticketing.repository.TrainRepository;
import com.siemens.trainticketing.repository.TrainScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final TrainScheduleRepository trainScheduleRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public AdminService(TrainRepository trainRepository,
                        StationRepository stationRepository,
                        TrainScheduleRepository trainScheduleRepository,
                        BookingRepository bookingRepository) {
        this.trainRepository = trainRepository;
        this.stationRepository = stationRepository;
        this.trainScheduleRepository = trainScheduleRepository;
        this.bookingRepository = bookingRepository;
    }

    public TrainResponse addTrain(TrainRequest request) {
        Train train = new Train();
        train.setName(request.name());
        train.setTotalSeats(request.totalSeats());
        Train saved = trainRepository.save(train);
        return new TrainResponse(saved.getId(), saved.getName(), saved.getTotalSeats());
    }

    public void deleteTrain(Long id) {
        if (!trainRepository.existsById(id)) {
            throw new TrainNotFoundException("Train with ID " + id + " not found");
        }
        trainRepository.deleteById(id);
    }

    public TrainResponse updateTrain(Long id, TrainRequest request) {
        Optional<Train> optionalTrain = trainRepository.findById(id);
        if (optionalTrain.isEmpty()) {
            throw new TrainNotFoundException("Train with ID " + id + " not found");
        }
        Train existingTrain = optionalTrain.get();
        existingTrain.setName(request.name());
        existingTrain.setTotalSeats(request.totalSeats());
        Train saved = trainRepository.save(existingTrain);
        return new TrainResponse(saved.getId(), saved.getName(), saved.getTotalSeats());
    }

    public StationResponse addStation(StationRequest request) {
        Station station = new Station();
        station.setName(request.name());
        Station saved = stationRepository.save(station);
        return new StationResponse(saved.getId(), saved.getName());
    }

    public StationResponse updateStation(Long id, StationRequest request) {
        Optional<Station> optionalStation = stationRepository.findById(id);
        if (optionalStation.isEmpty()) {
            throw new StationNotFoundException("Station with ID " + id + " not found");
        }

        Station existingStation = optionalStation.get();
        existingStation.setName(request.name());

        Station saved = stationRepository.save(existingStation);
        return new StationResponse(saved.getId(), saved.getName());
    }

    public void deleteStation(Long id) {
        if (!stationRepository.existsById(id)) {
            throw new StationNotFoundException("Station with ID " + id + " not found");
        }
        stationRepository.deleteById(id);
    }

    public TrainScheduleResponse addTrainSchedule(TrainScheduleRequest request) {
        Optional<Train> optionalTrain = trainRepository.findById(request.trainId());
        if (optionalTrain.isEmpty()) {
            throw new TrainNotFoundException("Train with ID " + request.trainId() + " not found");
        }

        Optional<Station> optionalStation = stationRepository.findById(request.stationId());
        if (optionalStation.isEmpty()) {
            throw new StationNotFoundException("Station with ID " + request.stationId() + " not found");
        }

        TrainSchedule schedule = new TrainSchedule();
        schedule.setTrain(optionalTrain.get());
        schedule.setStation(optionalStation.get());
        schedule.setStopOrder(request.stopOrder());
        schedule.setArrivalTime(request.arrivalTime());
        schedule.setDepartureTime(request.departureTime());

        TrainSchedule saved = trainScheduleRepository.save(schedule);
        return new TrainScheduleResponse(saved.getId(), saved.getTrain().getName(), saved.getStation().getName(), saved.getStopOrder(), saved.getArrivalTime(), saved.getDepartureTime());
    }

    public void removeTrainSchedule(Long scheduleId) {
        if (!trainScheduleRepository.existsById(scheduleId)) {
            throw new RuntimeException("Train Schedule with ID " + scheduleId + " not found");
        }
        trainScheduleRepository.deleteById(scheduleId);
    }

    public TrainScheduleResponse updateTrainSchedule(Long scheduleId, TrainScheduleRequest request) {
        Optional<TrainSchedule> optionalSchedule = trainScheduleRepository.findById(scheduleId);
        if (optionalSchedule.isEmpty()) {
            throw new RuntimeException("Train Schedule with ID " + scheduleId + " not found");
        }
        TrainSchedule existingSchedule = optionalSchedule.get();
        existingSchedule.setArrivalTime(request.arrivalTime());
        existingSchedule.setDepartureTime(request.departureTime());
        existingSchedule.setStopOrder(request.stopOrder());

        TrainSchedule saved = trainScheduleRepository.save(existingSchedule);
        return new TrainScheduleResponse(saved.getId(), saved.getTrain().getName(), saved.getStation().getName(), saved.getStopOrder(), saved.getArrivalTime(), saved.getDepartureTime());
    }

    public List<TrainScheduleResponse> getTrainRoute(Long trainId) {
        if (!trainRepository.existsById(trainId)) {
            throw new TrainNotFoundException("Train with ID " + trainId + " not found");
        }
        List<TrainSchedule> schedules = trainScheduleRepository.findByTrainIdOrderByStopOrderAsc(trainId);
        List<TrainScheduleResponse> responses = new ArrayList<>();
        for (TrainSchedule ts : schedules) {
            responses.add(new TrainScheduleResponse(ts.getId(), ts.getTrain().getName(), ts.getStation().getName(), ts.getStopOrder(), ts.getArrivalTime(), ts.getDepartureTime()));
        }
        return responses;
    }

    public List<BookingResponse> getBookingsForTrain(Long trainId) {
        if (!trainRepository.existsById(trainId)) {
            throw new TrainNotFoundException("Train with ID " + trainId + " not found");
        }
        List<Booking> bookings = bookingRepository.findByTrainId(trainId);
        List<BookingResponse> responses = new ArrayList<>();
        for (Booking b : bookings) {
            responses.add(new BookingResponse(b.getId(), b.getTrain().getName(), b.getStartStationName(), b.getEndStationName(),
                            b.getDepartureTime(), b.getArrivalTime(), b.getNumberOfTickets(), b.getCustomerEmail()));
        }
        return responses;
    }
}