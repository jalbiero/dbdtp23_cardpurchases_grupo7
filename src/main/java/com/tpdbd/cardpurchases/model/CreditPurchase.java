package com.tpdbd.cardpurchases.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;

@Entity
public class CreditPurchase extends Purchase {

    private float interest;

    private int numberOfQuotas;

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
        int numberOfQuotas)
    // @formatter:on
    {
        super(card, paymentVoucher, store, cuitStore, amount, finalAmount);
        this.interest = interest;
        this.numberOfQuotas = numberOfQuotas;
    }

    public float getInterest() {
        return this.interest;
    }

    public void setInterest(float interest) {
        this.interest = interest;
    }

    public int getNumberOfQuotas() {
        return this.numberOfQuotas;
    }

    @Override
    public boolean addQuota(Quota quota) {
        if (getQuotas().size() < this.numberOfQuotas)
            return super.addQuota(quota);

        return false;
    }

    // TODO Just for now, the number of quotas cannot be changed, because if the
    // number is less than getQuotas().size() it will be necessary to remove some
    // quotas, which ones?
    // public void setNumberOfQuotas(int numberOfQuotas) {
    // this.numberOfQuotas = numberOfQuotas;
    // }
}
