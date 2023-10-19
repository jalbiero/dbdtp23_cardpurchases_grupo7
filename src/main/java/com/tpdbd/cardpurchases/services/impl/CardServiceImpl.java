package com.tpdbd.cardpurchases.services.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.errors.CardNotFoundException;
import com.tpdbd.cardpurchases.model.Card;
import com.tpdbd.cardpurchases.repositories.CardRepository;
import com.tpdbd.cardpurchases.services.CardService;
import com.tpdbd.cardpurchases.util.StreamHelpers;

@Service
public class CardServiceImpl implements CardService {
    @Autowired
    private CardRepository cardRepository;

    @Override
    public List<Card> findSoonToExpire(LocalDate baseDate, Integer daysFromBaseDate) {
        var finalDate = baseDate.plusDays(daysFromBaseDate);
        return this.cardRepository
            .findByExpirationDateBetweenOrderByExpirationDate(baseDate, finalDate);
    }

    @Override
    public List<String> findAllIds() {
        return StreamHelpers.toStream(this.cardRepository.findAll())
            .map(card -> card.getId())
            .toList();
    }

    @Override
    public Card find(String id) {
        return this.cardRepository
            .findById(id)
            .orElseThrow(() -> new CardNotFoundException(id));
    }

    @Override
    public Card save(Card card) {
        return this.cardRepository.save(card);
    }

    @Override
    public void delete(String id) {
        this.cardRepository.deleteById(id);
    }
}
