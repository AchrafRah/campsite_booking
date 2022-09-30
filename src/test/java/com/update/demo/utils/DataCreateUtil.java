package com.update.demo.utils;

import com.update.demo.model.ReservationRequest;

import java.time.LocalDate;

public class DataCreateUtil {

    public static ReservationRequest createReservationRequest() {
        String email = "testReservation";
        String firstName = "first";
        String surName = "surname";
        LocalDate arrivalDate = LocalDate.now().plusDays(2);
        LocalDate departureDate = arrivalDate.plusDays(3);
        return new ReservationRequest(email, firstName, surName, arrivalDate, departureDate);
    }

}
