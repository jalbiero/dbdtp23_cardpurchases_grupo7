package com.tpdbd.cardpurchases.dto;

import java.time.LocalDate;

import jakarta.annotation.Nullable;

// This interface is a sort of namespace in order to group a set of DTO types
public interface RequestDTO {

    ////////////////////////////////////////////////////////
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
        boolean onlyCash)
    {
        public static com.tpdbd.cardpurchases.model.Discount toModel(Discount discount, 
                                                                     com.tpdbd.cardpurchases.model.Bank bank) 
        {
            return new com.tpdbd.cardpurchases.model.Discount(
                bank,
                discount.code(),
                discount.promotionTitle(),
                discount.nameStore(),
                discount.cuitStore(),
                discount.validityStartDate(),
                discount.validityEndDate(),
                discount.comments(),
                discount.discountPercentage(),
                discount.priceCap(),
                discount.onlyCash());
        }
    }

    ////////////////////////////////////////////////////////
    record PaymentDates(
        LocalDate firstExpiration, 
        LocalDate secondExpiration) 
    {}

    ////////////////////////////////////////////////////////
    record NextExpiredCards(
        @Nullable LocalDate baseDate,        // Today if it is not specified
        @Nullable Integer daysFromBaseDate)  // 30 days if it is not specified
    {}

}
