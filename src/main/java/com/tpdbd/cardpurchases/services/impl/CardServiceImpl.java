package com.tpdbd.cardpurchases.services.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tpdbd.cardpurchases.errors.CardNotFoundException;
import com.tpdbd.cardpurchases.model.Card;
import com.tpdbd.cardpurchases.repositories.CardRepository;
import com.tpdbd.cardpurchases.services.CardService;


@Service
public class CardServiceImpl implements CardService {
    @Autowired
    private CardRepository cardRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<Card> findSoonToExpire(LocalDate baseDate, Integer daysFromBaseDate) {
        var finalDate = baseDate.plusDays(daysFromBaseDate);
        return this.cardRepository
            .findByExpirationDateBetweenOrderByExpirationDate(baseDate, finalDate);
    }    

    @Override
    @Transactional(readOnly = true)
    public List<Long> findAllIds() {
        return this.cardRepository.findAllIds();
    }

    @Override
    @Transactional(readOnly = true)
    public Card find(long id) {
        return this.cardRepository
            .findById(id)
            .orElseThrow(() -> new CardNotFoundException(id));
    }

    @Override
    @Transactional
    public Card save(Card card) {
        return this.cardRepository.save(card);
    }

    @Override
    @Transactional
    public void delete(long id) {
        this.cardRepository.deleteById(id);
    }
}
