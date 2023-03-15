package com.tpdbd.cardpurchases.model;

import java.time.LocalDate;
import jakarta.persistence.Entity;

@Entity
public class Financing extends Promotion {

    private int numberOfQuotas;

    private float interes;

    public Financing(
    // @formatter:off
        String code, 
        String promotionTitle, 
        String nameStore, 
        String cuitStore, 
        LocalDate validityStartDate,
        LocalDate validityEndDate, 
        String comments, 
        int numberOfQuotas, 
        float interes) 
    // @formatter:on
    {
        super(code, promotionTitle, nameStore, cuitStore, validityStartDate, validityEndDate, comments);
        this.numberOfQuotas = numberOfQuotas;
        this.interes = interes;
    }

    public int getNumberOfQuotas() {
        return numberOfQuotas;
    }

    public void setNumberOfQuotas(int numberOfQuotas) {
        this.numberOfQuotas = numberOfQuotas;
    }

    public float getInteres() {
        return interes;
    }

    public void setInteres(float interes) {
        this.interes = interes;
    }
}
