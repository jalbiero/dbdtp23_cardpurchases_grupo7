package com.tpdbd.cardpurchases.services;

import java.util.List;

import com.tpdbd.cardpurchases.model.CardHolder;

public interface CardHolderService {
    CardHolder find(long id);
    
    List<String> findAllIds();
}
