package com.achraf.demo.controller;

import com.achraf.demo.model.Availability;
import com.achraf.demo.model.ReservationRequest;
import com.achraf.demo.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
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

    @Operation(summary = "Get a list of bookings")
    @GetMapping(path = "/availabilities", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Availability>> getBookings(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                          @Parameter(description = "arrival date requested, can be null. Must be in format yyyy-MM-dd")
                                                          @RequestParam(value = "arrivalDate", required = false) LocalDate arrivalDate,
                                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                          @Parameter(description = "departure date requested, can be null. Must be in format yyyy-MM-dd")
                                                          @RequestParam(value = "departureDate", required = false) LocalDate departureDate) {
        return ResponseEntity.ok(bookingService.getAvailableBookings(arrivalDate, departureDate));
    }

    @Operation(summary = "Create a new booking")
    @PostMapping(path = "/bookings", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createReservation(@RequestBody ReservationRequest reservationRequest) {
        return ResponseEntity.ok(bookingService.createReservation(reservationRequest));
    }

    @Operation(summary = "Update a booking")
    @PutMapping(path = "/bookings/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateReservation(@Parameter(description = "id of the registration to update")
                                                    @PathVariable String id,
                                                    @RequestBody ReservationRequest reservationRequest) {
        return ResponseEntity.ok(bookingService.updateReservation(id, reservationRequest));
    }

    @Operation(summary = "Delete a booking")
    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<Void> deleteReservation(@Parameter(description = "id of the registration to delete")
                                                  @PathVariable String id) {
        bookingService.deleteReservation(id);
        return ResponseEntity.ok().build();
    }

}
