package com.update.demo.demo.database.repository;

import com.update.demo.database.entity.ReservationEntity;
import org.springframework.data.repository.ListCrudRepository;

//@Repository
public interface ReservationRepository extends ListCrudRepository<ReservationEntity, Long> {

}