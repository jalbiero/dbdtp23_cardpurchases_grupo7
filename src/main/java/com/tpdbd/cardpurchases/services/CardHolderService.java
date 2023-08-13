package com.tpdbd.cardpurchases.services;

import java.util.List;

import com.tpdbd.cardpurchases.model.CardHolder;

public interface CardHolderService {
    CardHolder find(String id);
    
    List<String> findAllIds();
}
