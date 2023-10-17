package com.tpdbd.cardpurchases.model;

import java.util.Optional;

import jakarta.annotation.Nullable;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "purchase")
@TypeAlias("CashPurchase") // Simplifies name for queries (see CashPurchaseRepository)
public class CashPurchase extends Purchase {

    private float storeDiscount;

    // Note: Payment is nullable because the chicken and egg problem:
    //       1st: the purchase is made, 2nd: the payment is made
    @Nullable
    private Payment payment;

    private int month;

    private int year;

    public CashPurchase() {
    }

    public CashPurchase(
        Card card,
        @Nullable String paymentVoucher,
        String store,
        String cuitStore,
        float amount,
        float finalAmount,
        float storeDiscount,
        @Nullable Payment payment,
        int month,
        int year)
    {
        super(card, paymentVoucher, store, cuitStore, amount, finalAmount);
        this.storeDiscount = storeDiscount;
        this.payment = payment;
        this.month = month;
        this.year = year;
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
