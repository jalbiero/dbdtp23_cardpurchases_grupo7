package com.tpdbd.cardpurchases.services;

import java.time.LocalDate;
import java.util.List;

import com.tpdbd.cardpurchases.model.Card;

public interface CardService {
    Card find(String id);

    List<String> findAllIds();
    
    List<Card> findSoonToExpire(LocalDate baseDate, Integer daysFromBaseDate);

    Card save(Card card);

    void delete(String id);
}
