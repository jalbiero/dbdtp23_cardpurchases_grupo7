package com.tpdbd.cardpurchases.repositories;

import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.CardHolder;

// @todo Check its usage, maybe CrudRepository<CardHolder, Long> is simpler
public interface CardHolderRepository extends CrudRepository<CardHolder, Long> {
}
