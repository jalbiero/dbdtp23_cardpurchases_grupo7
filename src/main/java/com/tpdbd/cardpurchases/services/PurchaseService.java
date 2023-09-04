package com.tpdbd.cardpurchases.services;

import java.util.List;

import com.tpdbd.cardpurchases.model.Purchase;
import com.tpdbd.cardpurchases.repositories.projections.MostUsedVoucher;
import com.tpdbd.cardpurchases.repositories.projections.NumOfPurchasesByCard;

public interface PurchaseService {
    List<Long> findAllIds();
    List<Long> findAllCreditIds();
    
    Purchase findById(long purchaseId);

    Purchase findCreditTotalPrice(long purchaseId);

    List<NumOfPurchasesByCard> findTopPurchasers(int count);

    List<MostUsedVoucher> findTheMostUsedVouchers(int count);
}
