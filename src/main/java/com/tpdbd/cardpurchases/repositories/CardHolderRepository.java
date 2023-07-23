package com.tpdbd.cardpurchases.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.CardHolder;

public interface CardHolderRepository extends CrudRepository<CardHolder, Long> {
    @Query("SELECT c.id FROM CardHolder c")
    List<Long> findAllIds();
}
