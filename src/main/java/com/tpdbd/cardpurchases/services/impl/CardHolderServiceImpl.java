package com.tpdbd.cardpurchases.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.errors.CardHolderNotFoundException;
import com.tpdbd.cardpurchases.model.CardHolder;
import com.tpdbd.cardpurchases.repositories.CardHolderRepository;
import com.tpdbd.cardpurchases.services.CardHolderService;

@Service
public class CardHolderServiceImpl implements CardHolderService {
    @Autowired
    CardHolderRepository cardHolderRepository;

    @Override
    public CardHolder find(String dni) {
        return this.cardHolderRepository
            .findByDni(dni)
            .orElseThrow(() -> new CardHolderNotFoundException(dni));
    }

    @Override
    public List<String> findAllDnis() {
        return this.cardHolderRepository.findAllDnis();
    }
    
}
