package com.tpdbd.cardpurchases.model;

import java.util.Set;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;

@Entity
public class CreditPurchase extends Purchase {

    private float interest;

    //private int numberOfQuotas;

    public CreditPurchase() {
    }

    public CreditPurchase(
    // @formatter:off
        Card card, 
        @Nullable String paymentVoucher, 
        String store, 
        String cuitStore, 
        float amount,
        float finalAmount, 
        float interest, 
        Set<Quota> quotas) 
    // @formatter:on
    {
        super(card, paymentVoucher, store, cuitStore, amount, finalAmount, quotas);
        this.interest = interest;
        //this.numberOfQuotas = quotas.size();
    }

    public float getInterest() {
        return this.interest;
    }

    public void setInterest(float interest) {
        this.interest = interest;
    }

    public int getNumberOfQuotas() {
        //return numberOfQuotas;
        return this.getQuotas().size();
    }

    // public void setNumberOfQuotas(int numberOfQuotas) {
    //     this.numberOfQuotas = numberOfQuotas;
    // }
}
