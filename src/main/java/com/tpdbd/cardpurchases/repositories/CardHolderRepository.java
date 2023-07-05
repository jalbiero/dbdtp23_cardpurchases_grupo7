package com.tpdbd.cardpurchases.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.CardHolder;

public interface CardHolderRepository extends CrudRepository<CardHolder, Long> {
    Optional<CardHolder> findByDni(String dni);

    @Query("SELECT c.dni FROM CardHolder c")
    List<String> findAllDnis();
}
