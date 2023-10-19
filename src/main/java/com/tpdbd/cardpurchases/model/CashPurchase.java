package com.tpdbd.cardpurchases.model;

import java.util.Optional;

import jakarta.annotation.Nullable;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "purchase")
@TypeAlias("CashPurchase") // Simplifies name for queries (see CashPurchaseRepository)
public class CashPurchase extends Purchase {

    private float storeDiscount;

    // Payment is nullable because purchases (of any kind) are created first
    @Nullable
    private Payment payment;

    private int month;

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
        return storeDiscount;
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
