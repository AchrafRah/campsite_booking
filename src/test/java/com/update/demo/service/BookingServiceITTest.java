package com.update.demo.service;

import com.update.demo.database.repository.CampsiteBookingRepository;
import com.update.demo.database.repository.ReservationRepository;
import com.update.demo.model.Availability;
import com.update.demo.model.ReservationRequest;
import com.update.demo.utils.DataCreateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test to test concurrency, hpw the program behaves when many users try to make calls at the same time
 */
@SpringBootTest
class BookingServiceITTest {

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
    void testTwoUsersAtSameTime() {
        ReservationRequest userOneReq = DataCreateUtil.createReservationRequest();
        ReservationRequest userTwoReq = DataCreateUtil.createReservationRequest();

        CompletableFuture<String> firstReservation = CompletableFuture.supplyAsync(() -> bookingService.createReservation(userOneReq));
        CompletableFuture<String> secondReservation = CompletableFuture.supplyAsync(() -> bookingService.createReservation(userTwoReq));
        List<CompletableFuture<String>> allFutures = List.of(firstReservation, secondReservation);

        allFutures.forEach(future -> {
            try {
                future.join();
            } catch (CompletionException ex) {
                //Db Constraint issue
                assertThat(ex.getCause()).isOfAnyClassIn(DataIntegrityViolationException.class);
            }
        });
        //One success, the other one is in failure
        assertThat(allFutures.stream().filter(ft -> !ft.isCompletedExceptionally()).count()).isEqualTo(1);
    }


    @Test
    void testOneMonthBookingWithManyUsers() {

        List<ReservationRequest> reservationRequests = DataCreateUtil.createReservationRequestForAMonth();
        int processors = Runtime.getRuntime().availableProcessors();
        //Try the same requests for the same day in a multithreaded environment

        List<CompletableFuture<String>> allFutures = new ArrayList<>();

        for (ReservationRequest reservationRequest : reservationRequests) {

            for (int k = 0; k < processors; k++) {
                CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> bookingService.createReservation(reservationRequest));
                allFutures.add(future);
            }
        }

        allFutures.forEach(future -> {
            try {
                future.join();
            } catch (CompletionException ex) {
                assertThat(ex.getCause()).isOfAnyClassIn(DataIntegrityViolationException.class, ResponseStatusException.class);
            }
        });
        //All reservations must have succeeded,

        assertThat(allFutures.stream().filter(ft -> !ft.isCompletedExceptionally()).count()).isEqualTo(reservationRequests.size());
        List<Availability> bookings = bookingService.getBookings(LocalDate.now().plusDays(1), LocalDate.now().plusMonths(1));
        assertThat(bookings.stream().noneMatch(Availability::isAvailable)).isTrue();
    }
}