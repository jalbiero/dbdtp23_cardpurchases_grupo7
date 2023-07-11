package com.tpdbd.cardpurchases.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Card;

public interface CardRepository extends CrudRepository<Card, Long> {
    Optional<Card> findByNumber(String number);

    @Query("SELECT c.number FROM Card c")
    List<String> findAllNumbers();

    List<Card> findByExpirationDateBetweenOrderByExpirationDate(LocalDate dt1, LocalDate dt2);

    void deleteByNumber(String number);
}
