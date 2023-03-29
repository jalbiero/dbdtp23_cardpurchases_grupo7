package com.tpdbd.cardpurchases.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

@Entity
@Inheritance
public abstract class Purchase {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Card card;

    @Column(nullable = true)
    private String paymentVoucher;

    private String store;

    private String cuitStore;

    private float amount;

    private float finalAmount;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Quota> quotas;

    public Purchase() {
    }

    public Purchase(
    // @formatter:off        
        Card card, 
        @Nullable String paymentVoucher, 
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
        this.quotas = new LinkedHashSet<Quota>();
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Optional<String> getPaymentVoucher() {
        return Optional.ofNullable(this.paymentVoucher);
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

    public Set<Quota> getQuotas() {
        return Collections.unmodifiableSet(this.quotas);
    }

    public boolean addQuota(Quota quota) {
        return this.quotas.add(quota);
    }

    public boolean removeQuota(Quota quota) {
        return this.quotas.remove(quota);
    }

    @Override
    public String toString() {
        return "Purchase [id=" + id + ", card=" + card + ", paymentVoucher=" + paymentVoucher + ", store=" + store
                + ", cuitStore=" + cuitStore + ", amount=" + amount + ", finalAmount=" + finalAmount + ", quotas="
                + quotas + "]";
    }
}
