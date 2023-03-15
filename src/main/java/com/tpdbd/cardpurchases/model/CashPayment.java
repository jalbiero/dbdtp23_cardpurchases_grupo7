package com.tpdbd.cardpurchases.model;

import jakarta.persistence.Entity;

@Entity
public class CashPayment extends Purchase {

    private float storeDiscount;

    public CashPayment(
    // @formatter:off        
        Card card, 
        String paymentVoucher, 
        String store, 
        String cuitStore, 
        float amount, 
        float finalAmount,
        float storeDiscount) 
    // @formatter:on
    {
        super(card, paymentVoucher, store, cuitStore, amount, finalAmount);
        this.storeDiscount = storeDiscount;
    }

    public float getStoreDiscount() {
        return storeDiscount;
    }

    public void setStoreDiscount(float storeDiscount) {
        this.storeDiscount = storeDiscount;
    }
}
