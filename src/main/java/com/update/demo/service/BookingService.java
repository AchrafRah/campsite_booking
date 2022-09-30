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

    /**
     * Method to get all available bookings
     *
     * @param arrivalDate   arrival date, can be null
     * @param departureDate departure date, can be null
     * @return a list of available bookings for the time range
     */
    public List<Availability> getAvailableBookings(LocalDate arrivalDate, LocalDate departureDate) {
        return getBookings(arrivalDate, departureDate).stream().filter(Availability::isAvailable).collect(Collectors.toList());
    }


    /**
     * Method to get all  bookings
     *
     * @param arrivalDate   arrival date, can be null
     * @param departureDate departure date, can be null
     * @return a list of all bookings for the time range
     */
    public List<Availability> getBookings(LocalDate arrivalDate, LocalDate departureDate) {
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

    /**
     * Method to create a reservation
     *
     * @param reservationRequest reservation request parameters
     * @return a unique identifier for the reservation
     */
    @Transactional
    public String createReservation(ReservationRequest reservationRequest) {
        validationService.validateRequest(reservationRequest);
        List<Availability> availabilities = getBookings(reservationRequest.getArrivalDate(), reservationRequest.getDepartureDate());
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

        ReservationEntity reservation = new ReservationEntity();
        reservation.setCampsiteBooking(campsiteBookingEntities);
        ReservationEntity reservationEntity = saveReservation(reservationRequest, reservation);
        return String.valueOf(reservationEntity.getId());
    }

    /**
     * Method to delete a reservation
     *
     * @param requestId the reservation identifier
     */
    @Transactional
    public void deleteReservation(String requestId) {
        ReservationEntity reservationEntity = getReservationEntity(requestId);
        reservationEntity.getCampsiteBooking().forEach(campsiteBookingRepository::delete);
        reservationEntity.setActive(false);
        reservationRepository.save(reservationEntity);
    }

    /**
     * Method to update a reservation
     *
     * @param requestId          the reservation identifier
     * @param reservationRequest the new reservation request
     */
    @Transactional
    public String updateReservation(String requestId, ReservationRequest reservationRequest) {
        validationService.validateRequest(reservationRequest);
        ReservationEntity reservationEntity = getReservationEntity(requestId);

        return update(reservationRequest, reservationEntity);
    }

    private ReservationEntity getReservationEntity(String requestId) {
        Optional<ReservationEntity> reservation = reservationRepository.findById(Long.valueOf(requestId));
        ReservationEntity reservationEntity = reservation.orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "This booking id was not found: " + requestId));
        return reservationEntity;
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

        ReservationEntity savedEntity = saveReservation(reservationRequest, reservationEntity);
        return String.valueOf(savedEntity.getId());
    }


    private ReservationEntity saveReservation(ReservationRequest reservationRequest, ReservationEntity reservationEntity) {
        reservationEntity.setEmail(reservationRequest.getEmail());
        reservationEntity.setSurName(reservationRequest.getSurName());
        reservationEntity.setFirstName(reservationRequest.getFirstName());
        reservationEntity.setActive(true);
        return reservationRepository.save(reservationEntity);
    }

}
