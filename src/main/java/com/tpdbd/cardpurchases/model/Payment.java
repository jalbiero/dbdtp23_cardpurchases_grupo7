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

    private LocalDate firstExpiration;

    private LocalDate secondExpiration;

    private float surchase; // TODO "Purchase or Surcharge" ?

    private float totalPrice;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Quota> quotas;

    public Payment() {
    }

    public Payment(
    // @formatter:off        
        String code, 
        int month, 
        int year, 
        LocalDate firstExpiration, 
        LocalDate secondExpiration,
        float surchase, 
        float totalPrice) 
    // @formatter:on
    {
        this.code = code;
        this.month = month;
        this.year = year;
        this.firstExpiration = firstExpiration;
        this.secondExpiration = secondExpiration;
        this.surchase = surchase;
        this.totalPrice = totalPrice;
        this.quotas = new LinkedHashSet<Quota>();
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

    public float getSurchase() {
        return this.surchase;
    }

    public void setSurchase(float surchase) {
        this.surchase = surchase;
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

    public void addPurchase(Quota quota) {
        this.quotas.add(quota);
    }

    public void removePurchase(Quota quota) {
        this.quotas.remove(quota);
    }

    @Override
    public String toString() {
        return "Payment [id=" + id + ", code=" + code + ", month=" + month + ", year=" + year + ", firstExpiration="
                + firstExpiration + ", secondExpiration=" + secondExpiration + ", surchase=" + surchase
                + ", totalPrice=" + totalPrice + ", quotas=" + quotas + "]";
    }

}
