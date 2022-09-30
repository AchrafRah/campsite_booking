package com.achraf.demo.database.repository;

import com.achraf.demo.database.entity.CampsiteBookingEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CampsiteBookingRepository extends CrudRepository<CampsiteBookingEntity, Long> {

    @Query("Select c from CampsiteBookingEntity c where reservationDate >= ?1 and reservationDate <= ?2")
    List<CampsiteBookingEntity> findByDates(LocalDate beginDate, LocalDate endDate);

    @Modifying
    @Query("delete from CampsiteBookingEntity c where c.id = :id")
    void deleteById(@Param("id") Long id);
}