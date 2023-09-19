package com.tpdbd.cardpurchases.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tpdbd.cardpurchases.errors.CardHolderNotFoundException;
import com.tpdbd.cardpurchases.model.CardHolder;
import com.tpdbd.cardpurchases.repositories.CardHolderRepository;
import com.tpdbd.cardpurchases.services.CardHolderService;

@Service
public class CardHolderServiceImpl implements CardHolderService {
    @Autowired
    CardHolderRepository cardHolderRepository;

    @Override
    @Transactional(readOnly = true)
    public CardHolder find(long id) {
        return this.cardHolderRepository
            .findById(id)
            .orElseThrow(() -> new CardHolderNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findAllIds() {
        return this.cardHolderRepository.findAllIds();
    }
    
}
