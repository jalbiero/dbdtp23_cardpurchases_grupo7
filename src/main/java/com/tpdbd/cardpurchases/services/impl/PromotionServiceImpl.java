package com.tpdbd.cardpurchases.services.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.errors.PromotionNotFoundException;
import com.tpdbd.cardpurchases.model.Promotion;
import com.tpdbd.cardpurchases.repositories.PromotionRepository;
import com.tpdbd.cardpurchases.services.PromotionService;
import com.tpdbd.cardpurchases.util.StreamHelpers;

@Service
public class PromotionServiceImpl implements PromotionService {
    @Autowired
    private PromotionRepository promotionRepository;

    @Override
    public List<String> findAllCodes() {
        return StreamHelpers.toStream(this.promotionRepository.findByDeletedFalse())
            .map(promo -> promo.getCode())
            .toList();
    }
 
    @Override
    public Promotion findByCode(String code) {
        return this.promotionRepository
            .findByCodeAndDeletedFalse(code)
            .orElseThrow(() -> new PromotionNotFoundException(code));
    }
 
    @Override
    public List<Promotion> GetAvailablePromotions(String cuitStore, LocalDate from, LocalDate to) {
        return StreamHelpers.toStream(this.promotionRepository
            .findByCuitStoreAndValidityStartDateGreaterThanEqualAndValidityEndDateLessThanEqualAndDeletedFalse(
                cuitStore, from, to)).toList();
    }
 
    @Override
    public void deleteByCode(String code) {
        if (this.promotionRepository.deleteByCode(code) == 0)
            throw new PromotionNotFoundException(code);
    } 

    @Override
    public Promotion save(Promotion promotion) {
        return this.promotionRepository.save(promotion);
    }
}
