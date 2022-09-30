package com.achraf.demo.service;

import com.achraf.demo.model.ReservationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ValidationService {
    private static final String MIN_DAYS_NOT_RESPECTED = "\nBooking should be at least a day after today";
    private static final String MAX_DAYS_NOT_RESPECTED = "\nBooking should be at no more than one month away";
    private static final String MAX_STAY_NOT_RESPECTED = "\nBooking duration should ne no more than three days";

    public void validateRequest(ReservationRequest reservationRequest) {
        LocalDate now = LocalDate.now();
        LocalDate arrivalDate = reservationRequest.getArrivalDate();
        LocalDate departureDate = reservationRequest.getDepartureDate();
        boolean todayDate = ChronoUnit.DAYS.between(now, arrivalDate) < 1;
        boolean moreThanThreeDays = ChronoUnit.DAYS.between(arrivalDate, departureDate) > 3;
        boolean oneMonthOverToday = ChronoUnit.MONTHS.between(now, arrivalDate) >= 1 && arrivalDate.getDayOfMonth() > now.getDayOfMonth();

        if (todayDate || oneMonthOverToday || moreThanThreeDays) {

            String errorText = "";
            if (todayDate) {
                errorText += MIN_DAYS_NOT_RESPECTED;
            }
            if (oneMonthOverToday) {
                errorText += MAX_DAYS_NOT_RESPECTED;
            }
            if (moreThanThreeDays) {
                errorText += MAX_STAY_NOT_RESPECTED;
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorText);
        }
    }
}
