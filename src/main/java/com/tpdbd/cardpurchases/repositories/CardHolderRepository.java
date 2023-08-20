package com.tpdbd.cardpurchases.repositories;

import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.CardHolder;

public interface CardHolderRepository extends CrudRepository<CardHolder, String> {

}
