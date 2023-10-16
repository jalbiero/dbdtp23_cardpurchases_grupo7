package com.tpdbd.cardpurchases.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class CreditPurchase extends Purchase {

    @Column(nullable = true) // 'true' due to @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    private float interest;

    @Column(nullable = true) // 'true' due to @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    private int numberOfQuotas;

    @OneToMany(mappedBy = "purchase")
    private List<Quota> quotas;

    public CreditPurchase() {
    }

    public CreditPurchase(
        Card card,
        String paymentVoucher,
        String store,
        String cuitStore,
        float amount,
        float finalAmount,
        float interest,
        int numberOfQuotas)
    {
        super(card, paymentVoucher, store, cuitStore, amount, finalAmount);
        this.interest = interest;
        this.numberOfQuotas = numberOfQuotas;
        this.quotas = new ArrayList<Quota>();
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

    // TODO Just for now, the number of quotas cannot be changed, because if the number is less
    //      than getQuotas().size() it will be necessary to remove some quotas, which ones?
    // public void setNumberOfQuotas(int numberOfQuotas) {
    //     this.numberOfQuotas = numberOfQuotas;
    // }

    public List<Quota> getQuotas() {
        return this.quotas;
    }

    public void setQuotas(List<Quota> quotas) {
        this.quotas = quotas;
    }
}
