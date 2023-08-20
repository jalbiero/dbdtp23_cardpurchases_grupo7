package com.tpdbd.cardpurchases.services;

import java.time.LocalDate;
import java.util.List;

import com.tpdbd.cardpurchases.model.Promotion;

public interface PromotionService {
    List<String> findAllCodes();
 
    Promotion findByCode(String code);
 
    List<Promotion> GetAvailablePromotions(String cuitStore, LocalDate from, LocalDate to);
 
    void deleteByCode(String code);
    
    Promotion save(Promotion promotion);
}
