package com.tpdbd.cardpurchases.model;

import java.util.Optional;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

@Entity
public class Quota {
    @Id
    @GeneratedValue
    private Long id;

    private int number;

    private float price;

    private int month;

    private int year;

    @ManyToOne
    @JoinColumn
    @Nullable
    private Payment payment;

    public Quota() {
    }

    public Quota(int number, float price, int month, int year) {
        this(number, price, month, year, null);
    }

    public Quota(
    // @formatter:off
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
