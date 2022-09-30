package com.update.demo.service;

import com.update.demo.model.Availability;
import com.update.demo.model.ReservationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Test
    void testGetDefaultAvailabilities() {
        List<Availability> availabilities = bookingService.getAvailabilities(null, null);
        long availabilitiesCount = availabilities.stream().filter(Availability::isAvailable).count();
        assertThat(availabilitiesCount).isEqualTo(30);
    }

    @Test
    void testReservation() {
        String email = "testReservation";
        String firstName = "first";
        String surName = "surname";
        LocalDate arrivalDate = LocalDate.now().plusDays(2);
        LocalDate departureDate = arrivalDate.plusDays(3);
        ReservationRequest reservationRequest = new ReservationRequest(email, firstName, surName, arrivalDate, departureDate);
        String reservation = bookingService.createReservation(reservationRequest);
        List<Availability> availabilities = bookingService.getAvailabilities(null, null);
        long availabilitiesCount = availabilities.stream().filter(Availability::isAvailable).count();
        assertThat(availabilitiesCount).isEqualTo(26);
        assertThatCode(() -> bookingService.deleteReservation(reservation)).doesNotThrowAnyException();
    }

    @Test
    void testDeleteReservationNotFound() {
        assertThatCode(() -> bookingService.deleteReservation("11221231")).isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    void testUpdateReservation() {
        String email = "testUpdateReservation";
        String firstName = "first";
        String surName = "surname";
        LocalDate arrivalDate = LocalDate.now().plusDays(2);
        LocalDate departureDate = arrivalDate.plusDays(3);
        ReservationRequest reservationRequest = new ReservationRequest(email, firstName, surName, arrivalDate, departureDate);
        String reservation = bookingService.createReservation(reservationRequest);

        ReservationRequest newReservationReq  = new ReservationRequest(email, firstName, surName, arrivalDate.plusDays(1), departureDate);
        assertThatCode(() -> bookingService.updateReservation(reservation, newReservationReq)).doesNotThrowAnyException();
        assertThatCode(() -> bookingService.deleteReservation(reservation)).doesNotThrowAnyException();
    }
}