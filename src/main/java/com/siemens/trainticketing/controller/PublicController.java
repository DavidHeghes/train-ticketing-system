package com.siemens.trainticketing.controller;

import com.siemens.trainticketing.dto.BookingRequest;
import com.siemens.trainticketing.dto.BookingResponse;
import com.siemens.trainticketing.dto.RouteDTO;
import com.siemens.trainticketing.dto.StationResponse;
import com.siemens.trainticketing.service.BookingService;
import com.siemens.trainticketing.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class PublicController {

    private final RouteService routeService;
    private final BookingService bookingService;

    @Autowired
    public PublicController(RouteService routeService, BookingService bookingService) {
        this.routeService = routeService;
        this.bookingService = bookingService;
    }

    @GetMapping("/stations")
    public List<StationResponse> getAllStations() {
        return routeService.getAllStations();
    }

    @GetMapping("/routes")
    public List<RouteDTO> findRoutes(
            @RequestParam Long startStationId,
            @RequestParam Long endStationId) {
        return routeService.findRoutes(startStationId, endStationId);
    }

    @PostMapping("/bookings")
    public BookingResponse createBooking(@RequestBody BookingRequest request) {
        return bookingService.createBooking(request);
    }
}