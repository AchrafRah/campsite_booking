package com.update.demo.controller;

import com.update.demo.model.Availability;
import com.update.demo.model.ReservationRequest;
import com.update.demo.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/")
public class ReservationController {

    public final BookingService bookingService;

    public ReservationController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping(path = "/availabilities", produces = "application/json")
    public ResponseEntity<List<Availability>> getAvailabilities(LocalDate arrivalDate, LocalDate departureDate) {
        return ResponseEntity.ok(bookingService.getAvailabilities(arrivalDate, departureDate));
    }

    @PostMapping(path = "/bookings", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> createReservation(@RequestBody ReservationRequest reservationRequest) {
        return ResponseEntity.ok(bookingService.createReservation(reservationRequest));
    }

    @PutMapping(path = "/bookings/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> updateReservation(@PathVariable String id, @RequestBody ReservationRequest reservationRequest) {
        return ResponseEntity.ok(bookingService.updateReservation(id, reservationRequest));
    }

    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable String id) {
        bookingService.deleteReservation(id);
        return ResponseEntity.ok().build();
    }

}
