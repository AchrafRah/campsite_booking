package com.achraf.demo.utils;

import com.achraf.demo.date.DateIterator;
import com.achraf.demo.model.ReservationRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DataCreateUtil {

    public static ReservationRequest createReservationRequest() {
        String email = "testReservation";
        String firstName = "first";
        String surName = "surname";
        LocalDate arrivalDate = LocalDate.now().plusDays(2);
        LocalDate departureDate = arrivalDate.plusDays(3);
        return new ReservationRequest(email, firstName, surName, arrivalDate, departureDate);
    }

    public static List<ReservationRequest> createReservationRequestForAMonth() {
        String email = "testReservation";
        String firstName = "first";
        String surName = "surname";
        LocalDate arrivalDate = LocalDate.now().plusDays(1);
        LocalDate maxDate = arrivalDate.plusMonths(1).minusDays(1L);
        DateIterator dateIterator = new DateIterator(arrivalDate, maxDate);
        return dateIterator.stream()
                .map(date -> new ReservationRequest(email, firstName, surName, date, date))
                .collect(Collectors.toList());
    }
}
