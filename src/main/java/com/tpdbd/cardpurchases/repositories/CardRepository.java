package com.tpdbd.cardpurchases.repositories;

import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Card;

// @todo Check its usage, maybe CrudRepository<Card, Long> is simpler
public interface CardRepository extends CrudRepository<Card, Long> {
}
