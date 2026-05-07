package com.siemens.trainticketing.controller;

import com.siemens.trainticketing.dto.*;
import com.siemens.trainticketing.service.AdminService;
import com.siemens.trainticketing.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AdminController {

    private final AdminService adminService;
    private final BookingService bookingService;

    @Autowired
    public AdminController(AdminService adminService, BookingService bookingService) {
        this.adminService = adminService;
        this.bookingService = bookingService;
    }

    @PostMapping("/trains")
    public TrainResponse addTrain(@RequestBody TrainRequest request) {
        return adminService.addTrain(request);
    }

    @PutMapping("/trains/{id}")
    public TrainResponse updateTrain(@PathVariable Long id, @RequestBody TrainRequest request) {
        return adminService.updateTrain(id, request);
    }

    @DeleteMapping("/trains/{id}")
    public void deleteTrain(@PathVariable Long id) {
        adminService.deleteTrain(id);
    }

    @PostMapping("/stations")
    public StationResponse addStation(@RequestBody StationRequest request) {
        return adminService.addStation(request);
    }

    @PutMapping("/stations/{id}")
    public StationResponse updateStation(@PathVariable Long id, @RequestBody StationRequest request) {
        return adminService.updateStation(id, request);
    }

    @DeleteMapping("/stations/{id}")
    public void deleteStation(@PathVariable Long id) {
        adminService.deleteStation(id);
    }

    @PostMapping("/schedules")
    public TrainScheduleResponse addTrainSchedule(@RequestBody TrainScheduleRequest request) {
        return adminService.addTrainSchedule(request);
    }

    @PutMapping("/schedules/{id}")
    public TrainScheduleResponse updateTrainSchedule(@PathVariable Long id, @RequestBody TrainScheduleRequest request) {
        return adminService.updateTrainSchedule(id, request);
    }

    @DeleteMapping("/schedules/{id}")
    public void deleteTrainSchedule(@PathVariable Long id) {
        adminService.removeTrainSchedule(id);
    }

    @GetMapping("/trains/{id}/route")
    public List<TrainScheduleResponse> getTrainRoute(@PathVariable Long id) {
        return adminService.getTrainRoute(id);
    }

    @GetMapping("/trains/{id}/bookings")
    public List<BookingResponse> getBookingsForTrain(@PathVariable Long id) {
        return adminService.getBookingsForTrain(id);
    }

    @PostMapping("/trains/{id}/delay")
    public void notifyDelay(@PathVariable Long id, @RequestParam String message) {
        bookingService.notifyDelay(id, message);
    }
}