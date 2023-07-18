package com.tpdbd.cardpurchases.services.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.model.Promotion;
import com.tpdbd.cardpurchases.repositories.PromotionRepository;
import com.tpdbd.cardpurchases.services.PromotionService;


@Service
public class PromotionServiceImpl implements PromotionService {
    @Autowired
    private PromotionRepository promotionRepository;

    @Override
    public List<String> findAllCodes() {
        return this.promotionRepository.findAllCodes();
    }
 
    @Override
    public Optional<Promotion> findByCode(String code) {
        return this.promotionRepository.findByCode(code);
    }
 
    @Override
    public List<Promotion> GetAvailablePromotions(String cuitStore, LocalDate from, LocalDate to) {
        return this.promotionRepository
            .findByCuitStoreAndValidityStartDateGreaterThanEqualAndValidityEndDateLessThanEqual(
                cuitStore, from, to);
    }
 
    @Override
    public boolean deleteByCode(String code) {
        return this.promotionRepository.deleteByCode(code) > 0;
    } 
}
