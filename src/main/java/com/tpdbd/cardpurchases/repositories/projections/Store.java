package com.tpdbd.cardpurchases.repositories.projections;

// Mongo projections don't work with interfaces, this record replaces the interface
public record Store(String store, String cuitStore, float monthlyProfit) {

    // Provides compatibility with the SQL version based on the 
    // old Store interface

    public String getStore() {
        return this.store();
    }
    
    public String getCuitStore() {
        return this.cuitStore();
    }

    public float getMonthlyProfit() {
        return this.monthlyProfit();
    }

}
