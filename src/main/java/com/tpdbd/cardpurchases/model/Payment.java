package com.tpdbd.cardpurchases.model;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
public class Payment {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Purchase purchase;

    private String code;

    private int month;

    private int year;

    private LocalDate firstExpiration;

    private LocalDate secondExpiration;

    private float surchase; // TODO "Purchase or Surcharge" ?

    private float totalPrice;
   
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
    }

    public Purchase getPurchase() {
        return this.purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
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
        return firstExpiration;
    }

    public void setFirstExpiration(LocalDate firstExpiration) {
        this.firstExpiration = firstExpiration;
    }

    public LocalDate getSecondExpiration() {
        return secondExpiration;
    }

    public void setSecondExpiration(LocalDate secondExpiration) {
        this.secondExpiration = secondExpiration;
    }

    public float getSurchase() {
        return surchase;
    }

    public void setSurchase(float surchase) {
        this.surchase = surchase;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }
}
