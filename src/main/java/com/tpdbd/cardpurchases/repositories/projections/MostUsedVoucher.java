package com.tpdbd.cardpurchases.repositories.projections;

import com.tpdbd.cardpurchases.model.Promotion;

// Mongo projections don't work with interfaces, this record replaces the interface
public record MostUsedVoucher(Promotion promotion, int numOfPurchases) {

    // Provides compatibility with the SQL version based on the
    // old MostUsedVoucher interface

    public Promotion getPromotion() {
        return this.promotion;
    }

    public int getNumOfPurchases() {
        return this.numOfPurchases;
    }
}
