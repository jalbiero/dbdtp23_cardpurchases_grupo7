package com.tpdbd.cardpurchases.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Discount extends Promotion {

    @Column(nullable = true) // 'true' due to @Inheritance(strategy = InheritanceType.SINGLE_TABLE)  
    private float discountPercentage;

    @Column(nullable = true) // 'true' due to @Inheritance(strategy = InheritanceType.SINGLE_TABLE)  
    private float priceCap;

    @Column(nullable = true) // 'true' due to @Inheritance(strategy = InheritanceType.SINGLE_TABLE)  
    private boolean onlyCash; 

    public Discount() {
    }

    public Discount(
        Bank bank,
        String code, 
        String promotionTitle, 
        String nameStore, 
        String cuitStore, 
        LocalDate validityStartDate,
        LocalDate validityEndDate, 
        String comments, 
        float discountPercentage, 
        float priceCap, 
        boolean onlyCash) 
    {
        super(bank, code, promotionTitle, nameStore, cuitStore, validityStartDate, validityEndDate, comments);
        this.discountPercentage = discountPercentage;
        this.priceCap = priceCap;
        this.onlyCash = onlyCash;
    }

    public float getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(float discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public float getPriceCap() {
        return priceCap;
    }

    public void setPriceCap(float priceCap) {
        this.priceCap = priceCap;
    }

    public boolean isOnlyCash() {
        return onlyCash;
    }

    public void setOnlyCash(boolean onlyCash) {
        this.onlyCash = onlyCash;
    }
}
