package com.tpdbd.cardpurchases.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.CardHolder;

public interface CardHolderRepository extends CrudRepository<CardHolder, Long> {
    Optional<CardHolder> findByDni(String dni);
}
