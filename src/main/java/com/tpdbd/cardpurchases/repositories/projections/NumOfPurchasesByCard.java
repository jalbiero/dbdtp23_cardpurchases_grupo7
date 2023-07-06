package com.tpdbd.cardpurchases.repositories.projections;

import com.tpdbd.cardpurchases.model.Card;

public interface NumOfPurchasesByCard {
    int getNumOfPurchases();
    Card getCard();
}
