package com.tpdbd.cardpurchases.services;

import java.time.LocalDate;
import java.util.List;

import com.tpdbd.cardpurchases.model.Card;

public interface CardService {
    Card find(long id);

    List<Long> findAllIds();
    
    List<Card> findSoonToExpire(LocalDate baseDate, Integer daysFromBaseDate);

    Card save(Card card);

    void delete(long id);
}
