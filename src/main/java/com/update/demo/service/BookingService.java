package com.update.demo.service;

import com.update.demo.database.entity.CampsiteBookingEntity;
import com.update.demo.database.entity.ReservationEntity;
import com.update.demo.database.repository.CampsiteBookingRepository;
import com.update.demo.database.repository.ReservationRepository;
import com.update.demo.date.DateIterator;
import com.update.demo.model.Availability;
import com.update.demo.model.ReservationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private final CampsiteBookingRepository campsiteBookingRepository;

    private final ReservationRepository reservationRepository;
    private final ValidationService validationService;

    public BookingService(CampsiteBookingRepository campsiteBookingRepository, ReservationRepository reservationRepository, ValidationService validationService) {
        this.campsiteBookingRepository = campsiteBookingRepository;
        this.reservationRepository = reservationRepository;
        this.validationService = validationService;
    }

    public List<Availability> getAvailabilities(LocalDate arrivalDate, LocalDate departureDate) {
        if (arrivalDate == null) {
            arrivalDate = LocalDate.now().plusDays(1L);
        }
        if (departureDate == null) {
            departureDate = LocalDate.now().plusMonths(1L);
        }

        Set<LocalDate> reservedDates = campsiteBookingRepository.findByDates(arrivalDate, departureDate).stream().map(CampsiteBookingEntity::getReservationDate).collect(Collectors.toSet());
        DateIterator dateIterator = new DateIterator(arrivalDate, departureDate);
        return dateIterator.stream().map(currentDate -> new Availability(currentDate, !reservedDates.contains(currentDate)))
                .collect(Collectors.toList());

    }

    @Transactional
    public String createReservation(ReservationRequest reservationRequest) {
        validationService.validateRequest(reservationRequest);
        List<Availability> availabilities = getAvailabilities(reservationRequest.getArrivalDate(), reservationRequest.getDepartureDate());
        boolean isNotAvailableDuringPeriod = availabilities.stream().anyMatch(s -> !s.isAvailable());
        if (isNotAvailableDuringPeriod) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "A Booking already exists for the requested dates");
        }

        DateIterator dateIterator = new DateIterator(reservationRequest.getArrivalDate(), reservationRequest.getDepartureDate());

        List<CampsiteBookingEntity> campsiteBookingEntities = dateIterator.stream().map(currentDate ->
        {
            CampsiteBookingEntity campsiteBookingEntity = new CampsiteBookingEntity();
            campsiteBookingEntity.setReservationDate(currentDate);
            return campsiteBookingEntity;
        }).map(campsiteBookingRepository::save).collect(Collectors.toList());

        ReservationEntity entity = new ReservationEntity();
        entity.setCampsiteBooking(campsiteBookingEntities);
        entity.setEmail(reservationRequest.getEmail());
        entity.setSurName(reservationRequest.getSurName());
        entity.setFirstName(reservationRequest.getFirstName());
        entity.setActive(true);
        ReservationEntity reservationEntity = reservationRepository.save(entity);
        return String.valueOf(reservationEntity.getId());
    }

    @Transactional
    public void deleteReservation(String requestId) {
        reservationRepository.deleteById(Long.valueOf(requestId));
    }

    @Transactional
    public String updateReservation(String requestId, ReservationRequest reservationRequest) {
        validationService.validateRequest(reservationRequest);
        Optional<ReservationEntity> reservation = reservationRepository.findById(Long.valueOf(requestId));
        ReservationEntity reservationEntity = reservation.orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "This booking id was not found: " + requestId));

        return update(reservationRequest, reservationEntity);
    }

    private String update(ReservationRequest reservationRequest, ReservationEntity reservationEntity) {

        List<CampsiteBookingEntity> oldBookings = reservationEntity.getCampsiteBooking();
        Map<LocalDate, CampsiteBookingEntity> oldBookingsMap = oldBookings.stream().collect(Collectors.toMap(CampsiteBookingEntity::getReservationDate, Function.identity()));
        DateIterator dateIterator = new DateIterator(reservationRequest.getArrivalDate(), reservationRequest.getDepartureDate());

        List<CampsiteBookingEntity> campsiteBookingEntities = new ArrayList<>();

        for (LocalDate date : dateIterator) {
            if (oldBookingsMap.containsKey(date)) {
                campsiteBookingEntities.add(oldBookingsMap.get(date));
                oldBookingsMap.remove(date);
            } else {
                CampsiteBookingEntity campsiteBookingEntity = new CampsiteBookingEntity();
                campsiteBookingEntity.setReservationDate(date);
                campsiteBookingRepository.save(campsiteBookingEntity);
                campsiteBookingEntities.add(campsiteBookingEntity);
            }
        }

        Collection<CampsiteBookingEntity> bookingsToDelete = oldBookingsMap.values();
        bookingsToDelete.stream().forEach(campsiteBookingRepository::delete);

        reservationEntity.setCampsiteBooking(campsiteBookingEntities);
        reservationEntity.setActive(true);
        ReservationEntity savedEntity = reservationRepository.save(reservationEntity);
        return String.valueOf(savedEntity.getId());
    }
}
