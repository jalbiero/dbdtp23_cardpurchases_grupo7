package com.tpdbd.cardpurchases.repositories.projections;

// Mongo projections don't work with interfaces, this record replaces the interface
public record MostUsedVoucher(String code, int numOfPurchases) {

    // Provides compatibility with the SQL version based on the 
    // old MostUsedVoucher interface

    public String getCode() {
        return this.code;
    }

    public int getNumOfPurchases() {
        return this.numOfPurchases;
    }
}
