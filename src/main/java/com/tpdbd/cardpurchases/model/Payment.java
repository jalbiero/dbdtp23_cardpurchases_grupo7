package com.tpdbd.cardpurchases.model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
public class Payment {
    @Id
    @GeneratedValue
    private Long id;

    private String code;

    private int month;

    private int year;

    // TODO See comments in ResponseDTO.MonthlyPayment about these 3 attribues
    private LocalDate firstExpiration;
    private LocalDate secondExpiration;
    private float surcharge; 

    private float totalPrice;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Quota> quotas;

    // This allow an easy navigation to the card used in the payment (another
    // complex way it is to use one of the associated quotas in order to reach 
    // the Purchase and then the Card)
    @ManyToOne
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
        this.quotas = new LinkedHashSet<Quota>();
        this.card = card;
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

    public Set<Quota> getQuotas() {
        return Collections.unmodifiableSet(this.quotas);
    }

    public void addQuota(Quota quota) {
        this.quotas.add(quota);
    }

    public void removeQuota(Quota quota) {
        this.quotas.remove(quota);
    }

    public Card getCard() {
        return this.card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    @Override
    public String toString() {
        return "Payment [id=" + id + ", code=" + code + ", month=" + month + ", year=" + year + ", firstExpiration="
                + firstExpiration + ", secondExpiration=" + secondExpiration + ", surcharge=" + surcharge
                + ", totalPrice=" + totalPrice + ", quotas=" + quotas + ", card=" + card + "]";
    }

}
