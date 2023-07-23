package com.tpdbd.cardpurchases.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Card;

public interface CardRepository extends CrudRepository<Card, Long> {
    @Query("SELECT c.id FROM Card c")
    List<Long> findAllIds();

    List<Card> findByExpirationDateBetweenOrderByExpirationDate(LocalDate dt1, LocalDate dt2);
}
