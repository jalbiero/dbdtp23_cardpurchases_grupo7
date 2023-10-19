package com.tpdbd.cardpurchases.services;

import java.util.List;

import com.tpdbd.cardpurchases.model.Purchase;
import com.tpdbd.cardpurchases.repositories.projections.MostUsedPromotion;
import com.tpdbd.cardpurchases.repositories.projections.NumOfPurchasesByCard;

public interface PurchaseService {
    List<String> findAllIds();

    List<String> findAllCreditIds();

    Purchase findById(String purchaseId);

    Purchase findCreditById(String purchaseId);

    List<NumOfPurchasesByCard> findTopPurchasers(int count);

    List<MostUsedPromotion> findTheMostUsedPromotions(int count);
}
