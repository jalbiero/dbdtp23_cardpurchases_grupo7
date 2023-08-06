package com.tpdbd.cardpurchases.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.errors.CardHolderNotFoundException;
import com.tpdbd.cardpurchases.model.CardHolder;
import com.tpdbd.cardpurchases.repositories.CardHolderRepository;
import com.tpdbd.cardpurchases.services.CardHolderService;
import com.tpdbd.cardpurchases.util.StreamHelpers;

@Service
public class CardHolderServiceImpl implements CardHolderService {
    @Autowired
    CardHolderRepository cardHolderRepository;

    @Override
    public CardHolder find(long id) {
        return this.cardHolderRepository
            .findById(id)
            .orElseThrow(() -> new CardHolderNotFoundException(id));
    }

    @Override
    public List<String> findAllIds() {
        return StreamHelpers.toStream(this.cardHolderRepository.findAll())
            .map(cardHolder -> cardHolder.getId())
            .toList();
    }
    
}
