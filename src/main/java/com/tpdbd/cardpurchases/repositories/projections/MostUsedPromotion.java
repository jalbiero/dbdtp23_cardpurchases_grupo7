package com.tpdbd.cardpurchases.repositories.projections;

public interface MostUsedVoucher {
    int getNumOfPurchases();  
    String getCode();
}
