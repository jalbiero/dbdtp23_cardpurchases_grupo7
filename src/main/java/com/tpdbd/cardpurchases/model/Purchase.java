package com.tpdbd.cardpurchases.model;

import java.util.Optional;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Purchase {
    @Id
    @GeneratedValue
    @Column(name = "purchase_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(nullable = false)
    private String paymentVoucher;

    @Column(length = 50, nullable = false)
    private String store;

    @Column(length = 20, nullable = false)
    private String cuitStore;

    @Column(nullable = false)
    private float amount;

    @Column(nullable = false)
    private float finalAmount;

    @Nullable
    @ManyToOne
    @JoinColumn(name = "promotion_id", nullable = true)
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

    public long getId() {
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
