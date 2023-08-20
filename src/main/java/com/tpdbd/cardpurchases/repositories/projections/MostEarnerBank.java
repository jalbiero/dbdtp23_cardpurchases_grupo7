package com.tpdbd.cardpurchases.repositories.projections;

import com.tpdbd.cardpurchases.model.Bank;

// Mongo projections don't work with interfaces, this record replaces the interface
public record MostEarnerBank(Bank bank, float totalPaymentValue)  {

    // Provides compatibility with the SQL version based on the 
    // old NumOfPurchasesByCard interface

    public Bank getBank() {
        return this.bank;
    }

    public float getTotalPaymentValue() {
        return this.totalPaymentValue;
    }
}
