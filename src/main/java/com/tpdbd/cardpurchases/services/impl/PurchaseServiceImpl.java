package com.tpdbd.cardpurchases.services.impl;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.model.Purchase;
import com.tpdbd.cardpurchases.repositories.PurchaseRepository;
import com.tpdbd.cardpurchases.repositories.projections.MostUsedVoucher;
import com.tpdbd.cardpurchases.repositories.projections.NumOfPurchasesByCard;
import com.tpdbd.cardpurchases.services.PurchaseService;

@Service
public class PurchaseServiceImpl implements PurchaseService {
    @Autowired
    private PurchaseRepository<Purchase> purchaseRepository; 

    @Override
    public Optional<Purchase> findById(long purchaseId) {
        return this.purchaseRepository.findById(purchaseId);
    }

    @Override
    public Stream<NumOfPurchasesByCard> findTopPurchasers(int count) {
        var topPurchaserCards = this.purchaseRepository.findTopPurchaserCards(PageRequest.of(0, count));
        return topPurchaserCards.get();
    }

    @Override
    public Stream<MostUsedVoucher> findTheMostUsedVouchers(int count) {
        var mostUsedVouchers = this.purchaseRepository.findTheMostUsedVouchers(PageRequest.of(0, count));
        return mostUsedVouchers.get();
    }
}
