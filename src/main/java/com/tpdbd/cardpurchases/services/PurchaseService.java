package com.tpdbd.cardpurchases.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.tpdbd.cardpurchases.model.Purchase;
import com.tpdbd.cardpurchases.repositories.projections.MostUsedVoucher;
import com.tpdbd.cardpurchases.repositories.projections.NumOfPurchasesByCard;

public interface PurchaseService {
    List<Long> findAllIds();
    
    Optional<Purchase> findById(long purchaseId);

    Stream<NumOfPurchasesByCard> findTopPurchasers(int count);

    Stream<MostUsedVoucher> findTheMostUsedVouchers(int count);
}
