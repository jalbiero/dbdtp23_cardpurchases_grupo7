package com.tpdbd.cardpurchases.model;

import java.util.Optional;

import jakarta.persistence.*;

@Entity
public class Quota {
    @Id
    @GeneratedValue
    private Long id;

    private int number;

    private float price;

    private String month;

    private String year;

    @ManyToOne
    @JoinColumn
    private Payment payment;

    public Quota() {
    }

    public Quota(int number, float price, String month, String year) {
        this(number, price, month, year, null);
    }

    public Quota(int number, float price, String month, String year, Payment payment) {
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

    public String getMonth() {
        return this.month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Optional<Payment> getPayment() {
        return Optional.ofNullable(this.payment);
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
