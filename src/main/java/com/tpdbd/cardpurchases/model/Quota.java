package com.tpdbd.cardpurchases.model;

import java.util.Optional;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

@Entity
public class Quota {
    @Id
    @GeneratedValue
    @Column(name = "quota_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;

    @Column(nullable = false)
    private int number;

    @Column(nullable = false)
    private float price;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private int year;

    // Payment is nullable because purchases (of any kind) and their quotas are created first
    @Nullable
    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = true)
    private Payment payment;

    public Quota() {
    }

    public Quota(
        Purchase purchase,
        int number,
        float price,
        int month,
        int year)
    {
        this.purchase = purchase;
        this.number = number;
        this.price = price;
        this.month = month;
        this.year = year;
        this.payment = null; // no associated payment yet
    }

    public Purchase getPurchase() {
        return this.purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public float getPrice() {
        return this.price;
    }

    public void setPrice(float price) {
        this.price = price;
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

    public Optional<Payment> getPayment() {
        return Optional.ofNullable(this.payment);
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
