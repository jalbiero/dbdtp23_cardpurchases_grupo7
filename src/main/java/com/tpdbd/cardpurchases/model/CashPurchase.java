package com.tpdbd.cardpurchases.model;

import java.util.Set;

import jakarta.persistence.Entity;

@Entity
public class CashPurchase extends Purchase {

    private float storeDiscount;

    public CashPurchase() {
    }

    public CashPurchase(
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

    public CashPurchase(
    // @formatter:off        
        Card card, 
        String paymentVoucher, 
        String store, 
        String cuitStore, 
        float amount, 
        float finalAmount,
        float storeDiscount,
        Quota quota)
    // @formatter:on
    {
        super(card, paymentVoucher, store, cuitStore, amount, finalAmount, Set.of(quota));
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
