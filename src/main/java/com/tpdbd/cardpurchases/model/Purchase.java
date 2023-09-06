package com.tpdbd.cardpurchases.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)  
public abstract class Purchase {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Card card;

    @Column(nullable = true)
    private String paymentVoucher;

    @Column(length = 50, nullable = false)
    private String store;

    @Column(length = 20, nullable = false)
    private String cuitStore;

    @Column(nullable = false)
    private float amount;

    @Column(nullable = false)
    private float finalAmount;

    @OneToMany 
    @JoinTable(
        name="quota",
        joinColumns=@JoinColumn(name="purchase_id"),
        inverseJoinColumns=@JoinColumn(name="id"))
    private List<Quota> quotas;

    public Purchase() {
    }

    public Purchase(
        Card card, 
        @Nullable String paymentVoucher, 
        String store, 
        String cuitStore, 
        float amount, 
        float finalAmount) 
    {
        this.card = card;
        this.paymentVoucher = paymentVoucher;
        this.store = store;
        this.cuitStore = cuitStore;
        this.amount = amount;
        this.finalAmount = finalAmount;
        this.quotas = new ArrayList<Quota>();
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

    public List<Quota> getQuotas() {
        return this.quotas;
    }

    public boolean addQuota(Quota quota) {
        return this.quotas.add(quota);
    }

    // public boolean removeQuota(Quota quota) {
    //     return this.quotas.remove(quota);
    //}
}
