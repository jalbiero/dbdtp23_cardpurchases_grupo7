package com.tpdbd.cardpurchases.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class Payment {
    @Id
    @GeneratedValue
    @Column(name = "payment_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String code;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private LocalDate firstExpiration;

    @Column(nullable = false)
    private LocalDate secondExpiration;

    @Column(nullable = false)
    private float surcharge;

    @Column(nullable = false)
    private float totalPrice;

    @OneToMany(mappedBy = "payment")
    private List<Quota> quotas;

    @OneToMany(mappedBy = "payment")
    private List<CashPurchase> cashPurchases;

    // This allows an easy navigation to the card used in the payment (another
    // complex way for credit purchases it is to use one of the associated quotas in order to reach
    // the Purchase and then the Card)
    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    public Payment() {
    }

    public Payment(
        String code,
        int month,
        int year,
        LocalDate firstExpiration,
        LocalDate secondExpiration,
        float surcharge,
        float totalPrice,
        Card card)
    {
        this.code = code;
        this.month = month;
        this.year = year;
        this.firstExpiration = firstExpiration;
        this.secondExpiration = secondExpiration;
        this.surcharge = surcharge;
        this.totalPrice = totalPrice;
        this.card = card;
        this.quotas = new ArrayList<Quota>();
        this.cashPurchases = new ArrayList<CashPurchase>();
    }

    public Long getId() {
        return this.id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public LocalDate getFirstExpiration() {
        return this.firstExpiration;
    }

    public void setFirstExpiration(LocalDate firstExpiration) {
        this.firstExpiration = firstExpiration;
    }

    public LocalDate getSecondExpiration() {
        return this.secondExpiration;
    }

    public void setSecondExpiration(LocalDate secondExpiration) {
        this.secondExpiration = secondExpiration;
    }

    public float getSurcharge() {
        return this.surcharge;
    }

    public void setSurcharge(float surchase) {
        this.surcharge = surchase;
    }

    public float getTotalPrice() {
        return this.totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<Quota> getQuotas() {
        return this.quotas;
    }

    public void setQuotas(List<Quota> quotas) {
        this.quotas = quotas;
    }

    public List<CashPurchase> getCashPurchases() {
        return this.cashPurchases;
    }

    public void setCashPurchases(List<CashPurchase> purchases) {
        this.cashPurchases = purchases;
    }

    public Card getCard() {
        return this.card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
