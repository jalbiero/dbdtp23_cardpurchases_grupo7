package com.tpdbd.cardpurchases.services.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.tpdbd.cardpurchases.model.Card;
import com.tpdbd.cardpurchases.repositories.CardRepository;
import com.tpdbd.cardpurchases.services.CardService;

public class CardServiceImpl implements CardService {
    @Autowired
    private CardRepository cardRepository;
    
    @Override
    public List<Card> getSoonToExpire(LocalDate baseDate, Integer daysFromBaseDate) {
        var finalDate = baseDate.plusDays(daysFromBaseDate);
        return this.cardRepository
            .findByExpirationDateBetweenOrderByExpirationDate(baseDate, finalDate);
    }    
}
