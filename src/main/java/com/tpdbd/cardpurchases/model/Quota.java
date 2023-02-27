package com.tpdbd.cardpurchases.model;

import jakarta.persistence.Entity;

//@Entity
public class Quota {

    private  int number;

    private float price;

    private String month;

    private String year;

    public Quota(int number, float price, String month, String year) {
        this.number = number;
        this.price = price;
        this.month = month;
        this.year = year;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
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
}
