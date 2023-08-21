package com.tpdbd.cardpurchases.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.errors.CreditPurchaseNotFoundException;
import com.tpdbd.cardpurchases.errors.PurchaseNotFoundException;
import com.tpdbd.cardpurchases.model.Purchase;
import com.tpdbd.cardpurchases.repositories.CreditPurchaseRepository;
import com.tpdbd.cardpurchases.repositories.PurchaseRepository;
import com.tpdbd.cardpurchases.repositories.projections.MostUsedVoucher;
import com.tpdbd.cardpurchases.repositories.projections.NumOfPurchasesByCard;
import com.tpdbd.cardpurchases.services.PurchaseService;
import com.tpdbd.cardpurchases.util.StreamHelpers;

@Service
public class PurchaseServiceImpl implements PurchaseService {
    @Autowired
    private PurchaseRepository<Purchase> purchaseRepository; // For general use regarding the purchase type

    @Autowired
    private CreditPurchaseRepository creditPurchaseRepository; 

    @Override
    public List<String> findAllIds() {
        return StreamHelpers.toStream(this.purchaseRepository.findAll())
            .map(purchase -> purchase.getId())
            .toList();
    }

    @Override
    public List<String> findAllCreditIds() {
        return StreamHelpers.toStream(this.creditPurchaseRepository.findAll())
            .map(purchase -> purchase.getId())
            .toList();
    }

    @Override
    public Purchase findById(String purchaseId) {
        return this.purchaseRepository
            .findById(purchaseId)
            .orElseThrow(() -> new PurchaseNotFoundException(purchaseId));
    }

    @Override
    public Purchase findCreditById(String purchaseId) {
        return this.creditPurchaseRepository
            .findById(purchaseId)
            .orElseThrow(() -> new CreditPurchaseNotFoundException(purchaseId));    
}

    @Override
    public List<NumOfPurchasesByCard> findTopPurchasers(int count) {
        var topPurchaserCards = this.purchaseRepository.findTopPurchaserCards(PageRequest.of(0, count));
        return topPurchaserCards.get().toList();
    }

    @Override
    public List<MostUsedVoucher> findTheMostUsedVouchers(int count) {
        var mostUsedVouchers = this.purchaseRepository.findTheMostUsedVouchers(PageRequest.of(0, count));
        return mostUsedVouchers.get().toList();
    }
}
