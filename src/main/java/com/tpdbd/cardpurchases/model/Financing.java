package com.tpdbd.cardpurchases.model;

import java.time.LocalDate;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "promotion")
@TypeAlias("Financing")
public class Financing extends Promotion {

    private int numberOfQuotas;

    private float interest;

    public Financing() {
    }

    public Financing(
        Bank bank,
        String code, 
        String promotionTitle, 
        String nameStore, 
        String cuitStore, 
        LocalDate validityStartDate,
        LocalDate validityEndDate, 
        String comments, 
        int numberOfQuotas, 
        float interest) 
    {
        super(bank, code, promotionTitle, nameStore, cuitStore, validityStartDate, validityEndDate, comments);
        this.numberOfQuotas = numberOfQuotas;
        this.interest = interest;
    }

    public int getNumberOfQuotas() {
        return numberOfQuotas;
    }

    public void setNumberOfQuotas(int numberOfQuotas) {
        this.numberOfQuotas = numberOfQuotas;
    }

    public float getInterest() {
        return interest;
    }

    public void setInterest(float interest) {
        this.interest = interest;
    }

}
