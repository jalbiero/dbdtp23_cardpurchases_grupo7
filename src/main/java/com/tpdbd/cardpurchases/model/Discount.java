package com.tpdbd.cardpurchases.model;

import java.time.LocalDate;
import jakarta.persistence.Entity;

@Entity
public class Discount extends Promotion {

    private float discountPercentage;

    private float priceCap;

    // TODO What's the meaning of this attribute? It's supposed that a "Discount"
    // promotion only applies to cash purchases
    private boolean onlyCash; 

    public Discount() {
    }

    public Discount(
    // @formatter:off
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
    // @formatter:on
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

    @Override
    public String toString() {
        return "Discount [discountPercentage=" + discountPercentage + ", priceCap=" + priceCap + ", onlyCash="
                + onlyCash + "]";
    }
}
