package com.update.demo.service;

import com.update.demo.model.ReservationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {
    ValidationService validationService = new ValidationService();

    @Test
    void testInvalidReservationOnSameDay() {
        String email = "TestMail";
        String firstName = "first";
        String surName = "surname";
        LocalDate arrivalDate = LocalDate.now();
        LocalDate departureDate = arrivalDate.plusDays(3);
        ReservationRequest reservationRequest = new ReservationRequest(email, firstName, surName, arrivalDate, departureDate);
        assertThatCode(() -> validationService.validateRequest(reservationRequest)).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void testInvalidReservationTooFar() {
        String email = "TestMail";
        String firstName = "first";
        String surName = "surname";
        LocalDate arrivalDate = LocalDate.now().plusDays(30);
        LocalDate departureDate = arrivalDate.plusMonths(1);
        ReservationRequest reservationRequest = new ReservationRequest(email, firstName, surName, arrivalDate, departureDate);
        assertThatCode(() -> validationService.validateRequest(reservationRequest)).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void testInvalidReservationMoreThan3days() {
        String email = "TestMail";
        String firstName = "first";
        String surName = "surname";
        LocalDate arrivalDate = LocalDate.now().plusDays(10);
        LocalDate departureDate = arrivalDate.plusDays(5);
        ReservationRequest reservationRequest = new ReservationRequest(email, firstName, surName, arrivalDate, departureDate);
        assertThatCode(() -> validationService.validateRequest(reservationRequest)).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void testValidRequest() {
        String email = "TestMail";
        String firstName = "first";
        String surName = "surname";
        LocalDate arrivalDate = LocalDate.now().plusDays(10);
        LocalDate departureDate = arrivalDate.plusDays(3);
        ReservationRequest reservationRequest = new ReservationRequest(email, firstName, surName, arrivalDate, departureDate);
        assertThatCode(() -> validationService.validateRequest(reservationRequest)).doesNotThrowAnyException();
    }
}