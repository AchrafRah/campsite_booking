package com.update.demo.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

public class ReservationRequest implements Serializable {

    private final String email;
    private final String firstName;
    private final String surName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private final LocalDate arrivalDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private final LocalDate departureDate;

    public ReservationRequest(String email, String firstName, String surName, LocalDate arrivalDate, LocalDate departureDate) {
        this.email = email;
        this.firstName = firstName;
        this.surName = surName;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurName() {
        return surName;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }
}
