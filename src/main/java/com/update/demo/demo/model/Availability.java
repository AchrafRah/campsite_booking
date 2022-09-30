package com.update.demo.demo.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Availability implements Serializable {

    private final LocalDate date;
    private final boolean isAvailable;

    public Availability(LocalDate date, boolean isAvailable) {
        this.date = date;
        this.isAvailable = isAvailable;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
