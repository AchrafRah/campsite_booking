package com.achraf.demo.service;

import com.achraf.demo.database.repository.CampsiteBookingRepository;
import com.achraf.demo.database.repository.ReservationRepository;
import com.achraf.demo.model.Availability;
import com.achraf.demo.model.ReservationRequest;
import com.achraf.demo.utils.DataCreateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CampsiteBookingRepository campsiteBookingRepository;

    @BeforeEach
    public void beforeEach() {
        reservationRepository.deleteAll();
        campsiteBookingRepository.deleteAll();
    }


    @Test
    void testGetDefaultAvailabilities() {
        List<Availability> availabilities = bookingService.getAvailableBookings(null, null);
        LocalDate arrivalDate = LocalDate.now().plusDays(1L);
        LocalDate departureDate = LocalDate.now().plusMonths(1L).plusDays(1L);

        Long between = ChronoUnit.DAYS.between(arrivalDate, departureDate);
        assertThat(availabilities).hasSize(between.intValue());
    }

    @Test
    void testCreateReservationForThreeDays() {
        bookingService.createReservation(DataCreateUtil.createReservationRequest());
        List<Availability> availabilities = bookingService.getAvailableBookings(null, null);

        LocalDate arrivalDate = LocalDate.now().plusDays(1L);
        LocalDate departureDate = LocalDate.now().plusMonths(1L);
        assertThat(availabilities.size()).isEqualTo(ChronoUnit.DAYS.between(arrivalDate, departureDate) - 3/* 3 days reserved*/);
    }

    @Test
    void testDeleteReservationNotFound() {
        assertThatCode(() -> bookingService.deleteReservation("11221231")).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void testDeleteReservationMoreThanOnce() {
        String reservationId = bookingService.createReservation(DataCreateUtil.createReservationRequest());
        assertThatCode(() -> bookingService.deleteReservation(reservationId)).doesNotThrowAnyException();
        assertThatCode(() -> bookingService.deleteReservation(reservationId)).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("This booking id is archived");

    }

    @Test
    void testUpdateReservationAfterDeletion() {
        String reservationId = bookingService.createReservation(DataCreateUtil.createReservationRequest());
        assertThatCode(() -> bookingService.deleteReservation(reservationId)).doesNotThrowAnyException();
        assertThatCode(() -> bookingService.updateReservation(reservationId, DataCreateUtil.createReservationRequest())).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("This booking id is archived");

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

        ReservationRequest newReservationReq = new ReservationRequest(email, firstName, surName, arrivalDate.plusDays(1), departureDate);
        assertThatCode(() -> bookingService.updateReservation(reservation, newReservationReq)).doesNotThrowAnyException();
    }
}