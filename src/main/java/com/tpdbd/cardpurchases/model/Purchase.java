package com.tpdbd.cardpurchases.model;

import jakarta.persistence.*;

@Entity
@Inheritance
public abstract class Purchase {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Card card;

    private String paymentVoucher;

    private String store;

    private String cuitStore;

    private float amount;

    private float finalAmount;

    public Purchase(
    // @formatter:off        
        Card card, 
        String paymentVoucher, 
        String store, 
        String cuitStore, 
        float amount, 
        float finalAmount) 
    // @formatter:on
    {
        this.card = card;
        this.paymentVoucher = paymentVoucher;
        this.store = store;
        this.cuitStore = cuitStore;
        this.amount = amount;
        this.finalAmount = finalAmount;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public String getPaymentVoucher() {
        return paymentVoucher;
    }

    public void setPaymentVoucher(String paymentVoucher) {
        this.paymentVoucher = paymentVoucher;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getCuitStore() {
        return cuitStore;
    }

    public void setCuitStore(String cuitStore) {
        this.cuitStore = cuitStore;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(float finalAmount) {
        this.finalAmount = finalAmount;
    }
}
