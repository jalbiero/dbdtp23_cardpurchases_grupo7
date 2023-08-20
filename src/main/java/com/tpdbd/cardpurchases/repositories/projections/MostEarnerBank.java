package com.tpdbd.cardpurchases.repositories.projections;

// Mongo projections don't work with interfaces, this record replaces the interface
public record MostEarnerBank(String bankId, float totalPaymentValue)  {

    // Provides compatibility with the SQL version based on the 
    // old MostEarnerBank interface

    public String getBankId() {
        return this.bankId();
    }

    public float getTotalPaymentValue() {
        return this.totalPaymentValue;
    }
}
