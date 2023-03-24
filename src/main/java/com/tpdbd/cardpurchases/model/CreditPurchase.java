package com.tpdbd.cardpurchases.model;

import jakarta.persistence.Entity;

@Entity
public class CreditPurchase extends Purchase {

    private float interest;

    private int numberOfQuotas;

    public CreditPurchase(
    // @formatter:off
        Card card, 
        String paymentVoucher, 
        String store, 
        String cuitStore, 
        float amount,
        float finalAmount, 
        float interest, 
        int numberOfQuotas) 
    // @formatter:on
    {
        super(card, paymentVoucher, store, cuitStore, amount, finalAmount);
        this.interest = interest;
        this.numberOfQuotas = numberOfQuotas;
    }

    public float getInterest() {
        return interest;
    }

    public void setInterest(float interest) {
        this.interest = interest;
    }

    public int getNumberOfQuotas() {
        return numberOfQuotas;
    }

    public void setNumberOfQuotas(int numberOfQuotas) {
        this.numberOfQuotas = numberOfQuotas;
    }
}
