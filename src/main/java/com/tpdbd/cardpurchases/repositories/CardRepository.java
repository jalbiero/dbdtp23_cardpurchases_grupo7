package com.tpdbd.cardpurchases.repositories;

import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Card;

public interface CardRepository extends CrudRepository<Card, Long>  
{  
}  