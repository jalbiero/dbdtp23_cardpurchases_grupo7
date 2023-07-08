package com.tpdbd.cardpurchases.repositories.projections;

import com.tpdbd.cardpurchases.model.Bank;

public interface MostEarnerBank {
    float getTotalPaymentValue();
    Bank getBank();
}
