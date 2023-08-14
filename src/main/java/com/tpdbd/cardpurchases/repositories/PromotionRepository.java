package com.tpdbd.cardpurchases.repositories;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Promotion;

public interface PromotionRepository extends CrudRepository<Promotion, String> {
   Iterable<Promotion> findByDeletedFalse(); // findAll, but ignoring deleted records

   Optional<Promotion> findByCodeAndDeletedFalse(String code);

   Iterable<Promotion> findByCuitStoreAndValidityStartDateGreaterThanEqualAndValidityEndDateLessThanEqualAndDeletedFalse(String cuitStore, LocalDate from, LocalDate to);

   @Query("{ 'code': ?0 }")
   @Update("{ $set:  { 'deleted': true } }")
   Integer deleteByCode(String code);
}
