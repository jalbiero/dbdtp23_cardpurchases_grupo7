package com.tpdbd.cardpurchases.model;

import jakarta.persistence.Entity;

//@Entity
public class CashPayment extends Purchase {

    private float storeDiscount;

    public CashPayment(String paymentVoucher, String store, String cuitStore, float amount, float finalAmount,
            float storeDiscount) {
        super(paymentVoucher, store, cuitStore, amount, finalAmount);
        this.storeDiscount = storeDiscount;
    }

    public float getStoreDiscount() {
        return storeDiscount;
    }

    public void setStoreDiscount(float storeDiscount) {
        this.storeDiscount = storeDiscount;
    }
}
