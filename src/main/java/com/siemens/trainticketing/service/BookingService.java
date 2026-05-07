package com.siemens.trainticketing.service;

import com.siemens.trainticketing.dto.BookingRequest;
import com.siemens.trainticketing.dto.BookingResponse;
import com.siemens.trainticketing.entity.Booking;
import com.siemens.trainticketing.entity.Train;
import com.siemens.trainticketing.entity.TrainSchedule;
import com.siemens.trainticketing.exception.EndStationNotFoundException;
import com.siemens.trainticketing.exception.OverbookingException;
import com.siemens.trainticketing.exception.StartStationNotFoundException;
import com.siemens.trainticketing.exception.TrainNotFoundException;
import com.siemens.trainticketing.repository.BookingRepository;
import com.siemens.trainticketing.repository.TrainRepository;
import com.siemens.trainticketing.repository.TrainScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TrainRepository trainRepository;
    private final EmailService emailService;
    private final TrainScheduleRepository trainScheduleRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, TrainRepository trainRepository,
                          EmailService emailService, TrainScheduleRepository trainScheduleRepository) {
        this.bookingRepository = bookingRepository;
        this.trainRepository = trainRepository;
        this.emailService = emailService;
        this.trainScheduleRepository = trainScheduleRepository;
    }

    public BookingResponse createBooking(BookingRequest request) {
        Optional<Train> optionalTrain = trainRepository.findById(request.trainId());
        if (optionalTrain.isEmpty()) {
            throw new TrainNotFoundException("Train with ID " + request.trainId() + " not found");
        }

        Train train = optionalTrain.get();

        TrainSchedule startSched = trainScheduleRepository.findByTrainIdAndStationId(request.trainId(), request.startStationId());
        if (startSched == null) {
            throw new StartStationNotFoundException("Start station not found in schedule");
        }

        TrainSchedule endSched = trainScheduleRepository.findByTrainIdAndStationId(request.trainId(), request.endStationId());
        if (endSched == null) {
            throw new EndStationNotFoundException("End station not found in schedule");
        }

        List<Booking> existingBookings = bookingRepository.findByTrainId(request.trainId());
        int totalTicketsSold = 0;

        for (Booking b : existingBookings) {
            totalTicketsSold += b.getNumberOfTickets();
        }

        if (totalTicketsSold + request.numberOfTickets() > train.getTotalSeats()) {
            throw new OverbookingException("Overbooking! Seats left: " + (train.getTotalSeats() - totalTicketsSold));
        }

        Booking booking = new Booking();
        booking.setTrain(train);
        booking.setCustomerEmail(request.customerEmail());
        booking.setNumberOfTickets(request.numberOfTickets());

        booking.setStartStationName(startSched.getStation().getName());
        booking.setEndStationName(endSched.getStation().getName());

        booking.setDepartureTime(startSched.getDepartureTime());
        booking.setArrivalTime(endSched.getArrivalTime());

        Booking savedBooking = bookingRepository.save(booking);

        emailService.sendEmail(request.customerEmail(), "Booking Confirmation",
                "Hi! You have successfully booked " + request.numberOfTickets() + " tickets for train " + train.getName() +
                        "\nRoute: " + startSched.getStation().getName() + " (" + startSched.getDepartureTime() + ") -> " +
                        endSched.getStation().getName() + " (" + endSched.getArrivalTime() + ")" + "\n\nThank " +
                        "you for travelling with us!");

        return new BookingResponse(
                savedBooking.getId(),
                train.getName(),
                startSched.getStation().getName(),
                endSched.getStation().getName(),
                startSched.getDepartureTime(),
                endSched.getArrivalTime(),
                savedBooking.getNumberOfTickets(),
                savedBooking.getCustomerEmail()
        );
    }

    public void notifyDelay(Long trainId, String delayMessage) {
        if (!trainRepository.existsById(trainId)) {
            throw new TrainNotFoundException("Train with ID " + trainId + " not found");
        }

        List<Booking> bookings = bookingRepository.findByTrainId(trainId);
        for (Booking b : bookings) {
            emailService.sendEmail(b.getCustomerEmail(), "Delay Notification", delayMessage);
        }
    }
}