package com.tpdbd.cardpurchases.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.tpdbd.cardpurchases.model.Promotion;

import jakarta.transaction.Transactional;

public interface PromotionRepository extends CrudRepository<Promotion, Long> {
   @Query("SELECT p.code FROM Promotion p")
   List<String> findAllCodes();

   Optional<Promotion> findByCode(String code);

   List<Promotion> findByCuitStoreAndValidityStartDateGreaterThanEqualAndValidityEndDateLessThanEqual(String cuitStore, LocalDate from, LocalDate to);

   // Performs a logical delete
   @Transactional
   @Modifying
   @Query(
      "UPDATE Promotion p " + 
      "SET p.deleted = true " + 
      "WHERE p.code = :code")
   Integer deleteByCode(@Param("code") String code);
}
