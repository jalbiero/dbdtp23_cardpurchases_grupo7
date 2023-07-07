package com.tpdbd.cardpurchases.dto;

import java.time.LocalDate;

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
    record PaymentsUpdateDatesBody(
        LocalDate firstExpiration, 
        LocalDate secondExpiration) 
    {}

    ////////////////////////////////////////////////////////
    record CardsGetSoonToExpiredBody(
        LocalDate baseDate,        
        Integer daysFromBaseDate) 
    {
        public CardsGetSoonToExpiredBody() {
            this(LocalDate.now(), 30);
        }
    }

    ////////////////////////////////////////////////////////
    record Card(
        String bankCuit,
        String cardHolderDni,
        String number,
        String ccv,
        LocalDate since,
        LocalDate expirationDate)
    {}
}
