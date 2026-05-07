package com.siemens.trainticketing.service;

import com.siemens.trainticketing.dto.BookingRequest;
import com.siemens.trainticketing.dto.BookingResponse;
import com.siemens.trainticketing.entity.Booking;
import com.siemens.trainticketing.entity.Train;
import com.siemens.trainticketing.exception.OverbookingException;
import com.siemens.trainticketing.exception.TrainNotFoundException;
import com.siemens.trainticketing.repository.BookingRepository;
import com.siemens.trainticketing.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TrainRepository trainRepository;
    private final EmailService emailService;

    @Autowired
    public BookingService(BookingRepository bookingRepository, TrainRepository trainRepository, EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.trainRepository = trainRepository;
        this.emailService = emailService;
    }

    public BookingResponse createBooking(BookingRequest request) {
        Optional<Train> optionalTrain = trainRepository.findById(request.trainId());
        if (optionalTrain.isEmpty()) {
            throw new TrainNotFoundException("Train with ID " + request.trainId() + " not found");
        }

        Train train = optionalTrain.get();

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

        Booking savedBooking = bookingRepository.save(booking);

        emailService.sendEmail(request.customerEmail(), "Booking Confirmation",
                "Booked " + request.numberOfTickets() + " tickets for train " + train.getName());

        return new BookingResponse(savedBooking.getId(), train.getName(), savedBooking.getCustomerEmail(), savedBooking.getNumberOfTickets());
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