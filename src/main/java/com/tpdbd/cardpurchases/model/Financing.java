package com.tpdbd.cardpurchases.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Financing extends Promotion {

    @Column(nullable = true) // 'true' due to @Inheritance(strategy = InheritanceType.SINGLE_TABLE)  
    private int numberOfQuotas;

    @Column(nullable = true) // 'true' due to @Inheritance(strategy = InheritanceType.SINGLE_TABLE)  
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
