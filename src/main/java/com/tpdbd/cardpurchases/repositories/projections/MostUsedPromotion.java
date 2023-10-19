package com.tpdbd.cardpurchases.repositories.projections;

import com.tpdbd.cardpurchases.model.Promotion;

public interface MostUsedPromotion {
    int getNumOfPurchases();
    Promotion getPromotion();
}
