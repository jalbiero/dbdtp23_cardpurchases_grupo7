package com.tpdbd.cardpurchases.controllers.util;

import java.time.LocalDate;

import jakarta.annotation.Nullable;

/**
 * Provides records for modelling controller parameters (when classes from 
 * 'model' are not enough)
 */
public interface Params {
    record Discount(
        String code, 
        String promotionTitle, 
        String nameStore, 
        String cuitStore, 
        LocalDate validityStartDate,
        LocalDate validityEndDate, 
        String comments, 
        float discountPercentage, 
        float priceCap, 
        boolean onlyCash) {}

    record PaymentDates(
        LocalDate firstExpiration, 
        LocalDate secondExpiration) {}

    record NextExpiredCards(
        @Nullable LocalDate baseDate,       // Today if it is not specified
        @Nullable Integer daysFromBaseDate  // 30 days if it is not specified
    ) {}

}
