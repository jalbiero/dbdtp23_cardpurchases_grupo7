package com.tpdbd.cardpurchases.repositories.projections;

import com.tpdbd.cardpurchases.model.Card;

// Mongo projections don't work with interfaces, this record replaces the interface
public record NumOfPurchasesByCard(Card card, int numOfPurchases) {

    // Provides compatibility with the SQL version based on the 
    // old NumOfPurchasesByCard interface

    public Card getCard() {
        return this.card();
    }

    public int getNumOfPurchases() {
        return this.numOfPurchases();
    }
}
