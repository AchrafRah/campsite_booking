package com.update.demo.demo.database.repository;

import com.update.demo.database.entity.CampsiteBookingEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

//@Repository
public interface CampsiteBookingRepository extends ListCrudRepository<CampsiteBookingEntity, Long> {

    @Query("Select c from CampsiteBookingEntity c where reservationDate >= ?1 and reservationDate <= ?2")
    List<CampsiteBookingEntity> findByDates(LocalDate beginDate, LocalDate endDate);

    @Modifying
    @Query("delete from CampsiteBookingEntity c where c.id = :id")
    void deleteById(@Param("id") Long id);
}