package com.tpdbd.cardpurchases.model;

import java.util.Optional;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

@Entity
public class Quota {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Purchase purchase;

    private int number;

    private float price;

    private int month;

    private int year;

    @ManyToOne
    @JoinColumn(nullable = true)
    @Nullable
    private Payment payment;

    public Quota() {
    }

    public Quota(Purchase purchase, int number, float price, int month, int year) {
        this(purchase, number, price, month, year, null);
    }

    public Quota(
    // @formatter:off
        Purchase purchase,
        int number, 
        float price, 
        int month, 
        int year, 
        Payment payment)
    // @formatter:on
    {
        this.number = number;
        this.price = price;
        this.month = month;
        this.year = year;
        this.payment = payment;
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

    @Override
    public String toString() {
        return "Quota [id=" + id + ", purchase=" + purchase + ", number=" + number + ", price=" + price + ", month="
                + month + ", year=" + year + ", payment=" + payment + "]";
    }
}
