package com.tpdbd.cardpurchases.model;

import java.time.LocalDate;
import jakarta.persistence.Entity;

//@Entity
public class Payment {

    private String code;

    private String month;

    private String year;

    private LocalDate firstExpiration;

    private LocalDate secondExpiration;

    private float surchase;

    private float totalPrice;

    public Payment(String code, String month, String year, LocalDate firstExpiration, LocalDate secondExpiration, float surchase, float totalPrice) {
        this.code = code;
        this.month = month;
        this.year = year;
        this.firstExpiration = firstExpiration;
        this.secondExpiration = secondExpiration;
        this.surchase = surchase;
        this.totalPrice = totalPrice;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
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
