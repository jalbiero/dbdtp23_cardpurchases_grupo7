package com.tpdbd.cardpurchases.dto;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

public interface ResponseDTO {

    ////////////////////////////////////////////////////////
    record Bank(
        String name,
        String cuit,
        String address,
        String telephone,
        Set<Promotion> promotions)
    {
        public static Bank fromModel(com.tpdbd.cardpurchases.model.Bank bank) {
            return new Bank(
                bank.getName(),
                bank.getCuit(),
                bank.getAddress(),
                bank.getTelephone(),
                bank.getPromotions().stream()
                    .map(Promotion::fromModel)
                    .collect(Collectors.toSet()));
        }
    }

    ////////////////////////////////////////////////////////
    record Card(
        String number,
        String ccv,
        String cardholderNameInCard,
        LocalDate since,
        LocalDate expirationDate,
        String bankCuit, // In real life part of the card number identifies the bank
        String userDni) // In real life this is not necessary, the name provides a partial identificaion
    {
        public static Card fromModel(com.tpdbd.cardpurchases.model.Card card) {
            return new Card(
                card.getNumber(), 
                card.getCcv(), 
                card.getCardholderNameInCard(), 
                card.getSince(),
                card.getExpirationDate(),
                card.getBank().getCuit(),
                card.getCardHolder().getDni());
        }
    }

    ////////////////////////////////////////////////////////
    record Payment(
        String code,
        int month,
        int year,
        LocalDate firstExpiration,
        LocalDate secondExpiration,
        float surchase,
        float totalPrice,
        Set<Quota> quotas)
    {
        public static Payment fromModel(com.tpdbd.cardpurchases.model.Payment payment) {
            return new Payment(
                payment.getCode(),
                payment.getMonth(),
                payment.getYear(),
                payment.getFirstExpiration(), 
                payment.getSecondExpiration(), 
                payment.getSurchase(), 
                payment.getTotalPrice(),
                payment.getQuotas().stream()
                    .map(Quota::fromModel)
                    .collect(Collectors.toSet()));
        }
    }


    ////////////////////////////////////////////////////////
    record Promotion(
        String code,
        String promotionTitle,
        String nameStore,
        String cuitStore,
        LocalDate validityStartDate,
        LocalDate validityEndDate,
        String comments)
    {
        public static Promotion fromModel(com.tpdbd.cardpurchases.model.Promotion promotion) {
            return new Promotion(
                promotion.getCode(), 
                promotion.getPromotionTitle(), 
                promotion.getNameStore(), 
                promotion.getCuitStore(), 
                promotion.getValidityStartDate(),
                promotion.getValidityEndDate(),
                promotion.getComments());
        } 
    }

    ////////////////////////////////////////////////////////
    record Quota(
        int number, 
        float price, 
        int month, 
        int year, 
        String store,
        String cardNumber)
    {
        public static Quota fromModel(com.tpdbd.cardpurchases.model.Quota quota) {
            return new Quota(
                quota.getNumber(), 
                quota.getPrice(), 
                quota.getMonth(), 
                quota.getYear(),
                quota.getPurchase().getStore(),
                quota.getPurchase().getCard().getNumber());
        }
    }
}
