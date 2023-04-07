package com.tpdbd.cardpurchases.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Card;

public interface CardRepository extends CrudRepository<Card, Long> {
    Optional<Card> findByNumber(String number);

    @Query("SELECT u.number FROM Card u")
    List<String> findAllNumbers();

    List<Card> findByExpirationDateBetween(LocalDate dt1, LocalDate dt2);
}
