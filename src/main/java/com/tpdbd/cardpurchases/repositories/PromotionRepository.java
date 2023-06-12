package com.tpdbd.cardpurchases.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.Repository;

import com.tpdbd.cardpurchases.model.Promotion;

public interface PromotionRepository extends Repository<Promotion, Long> {
   List<Promotion> findByCuitStoreAndValidityStartDateGreaterThanEqualAndValidityEndDateLessThanEqual(String cuitStore, LocalDate from, LocalDate to);
}
