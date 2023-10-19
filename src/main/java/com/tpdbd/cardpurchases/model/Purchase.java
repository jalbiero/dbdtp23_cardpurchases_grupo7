package com.tpdbd.cardpurchases.model;

import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.annotation.Nullable;

@Document
public abstract class Purchase {
    @Id
    private String id;

    // Embedded to simplify queries (see PurchaseRepository)
    private Card card;

    private String paymentVoucher;

    private String store;

    private String cuitStore;

    private float amount;

    private float finalAmount;

    @Nullable
    private Promotion validPromotion;

    public Purchase() {
    }

    public Purchase(
        Card card,
        String paymentVoucher,
        String store,
        String cuitStore,
        float amount,
        float finalAmount,
        @Nullable Promotion validPromotion)
    {
        this.card = card;
        this.paymentVoucher = paymentVoucher;
        this.store = store;
        this.cuitStore = cuitStore;
        this.amount = amount;
        this.finalAmount = finalAmount;
        this.validPromotion = validPromotion;
    }

    public String getId() {
        return this.id;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public String getPaymentVoucher() {
        return this.paymentVoucher;
    }

    public void setPaymentVoucher(String paymentVoucher) {
        this.paymentVoucher = paymentVoucher;
    }

    public String getStore() {
        return this.store;
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
        return this.amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getFinalAmount() {
        return this.finalAmount;
    }

    public void setFinalAmount(float finalAmount) {
        this.finalAmount = finalAmount;
    }

    public Optional<Promotion> getValidPromotion() {
        return Optional.of(this.validPromotion);
    }

    public void setValidPromotion(Promotion validPromotion) {
        this.validPromotion = validPromotion;
    }
}
