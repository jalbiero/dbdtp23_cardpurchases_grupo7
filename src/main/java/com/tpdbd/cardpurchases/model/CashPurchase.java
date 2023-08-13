package com.tpdbd.cardpurchases.model;

import jakarta.annotation.Nullable;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class CashPurchase extends Purchase {

    //@Column(nullable = false)
    private float storeDiscount;

    public CashPurchase() {
    }

    public CashPurchase(
        Card card, 
        @Nullable String paymentVoucher, 
        String store, 
        String cuitStore, 
        float amount, 
        float finalAmount,
        float storeDiscount) 
    {
        super(card, paymentVoucher, store, cuitStore, amount, finalAmount);
        this.storeDiscount = storeDiscount;
    }

    @Override
    public boolean addQuota(Quota quota) {
        // Cash purchases only have 1 quota
        if (getQuotas().size() == 0)
            return super.addQuota(quota);

        return false;
    }

    public float getStoreDiscount() {
        return storeDiscount;
    }

    public void setStoreDiscount(float storeDiscount) {
        this.storeDiscount = storeDiscount;
    }

}
