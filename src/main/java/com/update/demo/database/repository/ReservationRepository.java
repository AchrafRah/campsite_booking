package com.update.demo.database.repository;

import com.update.demo.database.entity.ReservationEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends ListCrudRepository<ReservationEntity, Long> {

}