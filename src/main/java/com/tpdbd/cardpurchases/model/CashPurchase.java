package com.tpdbd.cardpurchases.model;

import java.util.Optional;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class CashPurchase extends Purchase {
    @Column(nullable = true) // 'true' due to @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    private float storeDiscount;

    // Payment is nullable because purchases (of any kind) are created first
    @Nullable
    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = true)
    private Payment payment;

    @Column(nullable = true) // 'true' due to @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    private int month;

    @Column(nullable = true) // 'true' due to @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    private int year;

    public CashPurchase() {
    }

    public CashPurchase(
        Card card,
        String paymentVoucher,
        String store,
        String cuitStore,
        float amount,
        float finalAmount,
        float storeDiscount,
        int month,
        int year,
        @Nullable Promotion validPromotion)
    {
        super(card, paymentVoucher, store, cuitStore, amount, finalAmount, validPromotion);
        this.storeDiscount = storeDiscount;
        this.month = month;
        this.year = year;
        this.payment = null; // no associated payment yet
    }

    public float getStoreDiscount() {
        return this.storeDiscount;
    }

    public void setStoreDiscount(float storeDiscount) {
        this.storeDiscount = storeDiscount;
    }

    public Optional<Payment> getPayment() {
        return Optional.of(this.payment);
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public int getMonth() {
        return this.month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
