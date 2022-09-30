package com.update.demo.service;

import com.update.demo.model.ReservationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;

@Service
public class ValidationService {
    private static final String MIN_DAYS_NOT_RESPECTED = "\nBooking should be at least a day after today";
    private static final String MAX_DAYS_NOT_RESPECTED = "\nBooking should be at no more than one month away";
    private static final String MAX_STAY_NOT_RESPECTED = "\nBooking duration should ne no more than three days";

    public void validateRequest(ReservationRequest reservationRequest) {
        LocalDate now = LocalDate.now();
        LocalDate arrivalDate = reservationRequest.getArrivalDate();
        LocalDate departureDate = reservationRequest.getDepartureDate();
        boolean minDays = Period.between(now, arrivalDate).getDays() < 1 &&  Period.between(departureDate, now).getMonths() == 0;
        boolean maxDays = Period.between(now, departureDate).getMonths() > 1;
        boolean noMoreThanThreeDays = Period.between(arrivalDate, departureDate).getDays() > 3;

        if (minDays || maxDays || noMoreThanThreeDays) {

            String errorText = "";
            if (minDays) {
                errorText += MIN_DAYS_NOT_RESPECTED;
            }
            if (maxDays) {
                errorText += MAX_DAYS_NOT_RESPECTED;
            }
            if (noMoreThanThreeDays) {
                errorText += MAX_STAY_NOT_RESPECTED;
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorText);
        }
    }
}
