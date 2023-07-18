package com.tpdbd.cardpurchases.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.tpdbd.cardpurchases.model.Promotion;

public interface PromotionService {
    List<String> findAllCodes();
 
    Optional<Promotion> findByCode(String code);
 
    List<Promotion> GetAvailblePromotions(String cuitStore, LocalDate from, LocalDate to);
 
    boolean deleteByCode(String code);    
}
