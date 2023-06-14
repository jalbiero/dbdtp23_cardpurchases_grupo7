package com.tpdbd.cardpurchases.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


// This interface is a sort of namespace in order to group a set of DTO types
public interface ResponseDTO {

    ////////////////////////////////////////////////////////
    record Bank(
        String name,
        String cuit,
        String address,
        String telephone,
        List<Promotion> promotions)
    {
        public static Bank fromModel(com.tpdbd.cardpurchases.model.Bank bank) {
            return new Bank(
                bank.getName(),
                bank.getCuit(),
                bank.getAddress(),
                bank.getTelephone(),
                bank.getPromotions().stream()
                    .map(Promotion::fromModel)
                    .toList());
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
        List<Quota> quotas)
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
                    .toList());
        }
    }


    ////////////////////////////////////////////////////////
    interface Promotion {
        String getType();

        static Promotion fromModel(com.tpdbd.cardpurchases.model.Promotion promotion) {
            return switch (promotion) {
                case com.tpdbd.cardpurchases.model.Discount d -> Discount.fromModel(d);
                case com.tpdbd.cardpurchases.model.Financing f -> Financing.fromModel(f);
                default -> throw new IllegalArgumentException("Unknow type of promotion");
            };
        }
    }
    
    record Discount(
        String code,
        String promotionTitle,
        String nameStore,
        String cuitStore,
        LocalDate validityStartDate,
        LocalDate validityEndDate,
        String comments,
        float discountPercentage,
        float priceCap)
        implements Promotion
    {
        public static Discount fromModel(com.tpdbd.cardpurchases.model.Discount discount) {
            return new Discount(
                discount.getCode(), 
                discount.getPromotionTitle(), 
                discount.getNameStore(), 
                discount.getCuitStore(), 
                discount.getValidityStartDate(),
                discount.getValidityEndDate(),
                discount.getComments(),
                discount.getDiscountPercentage(),
                discount.getPriceCap());
        } 

        @Override
        public String getType() {
            return this.getClass().getSimpleName();
        }
    }

    record Financing(
        String code,
        String promotionTitle,
        String nameStore,
        String cuitStore,
        LocalDate validityStartDate,
        LocalDate validityEndDate,
        String comments,
        int numberOfQuotas,
        float interest)
        implements Promotion
    {
        public static Financing fromModel(com.tpdbd.cardpurchases.model.Financing financing) {
            return new Financing(
                financing.getCode(), 
                financing.getPromotionTitle(), 
                financing.getNameStore(), 
                financing.getCuitStore(), 
                financing.getValidityStartDate(),
                financing.getValidityEndDate(),
                financing.getComments(),
                financing.getNumberOfQuotas(),
                financing.getInterest());
        }

        @Override
        public String getType() {
            return this.getClass().getSimpleName();
        }
    }

    ////////////////////////////////////////////////////////
    interface Purchase {
        String getType();

        static Purchase fromModel(com.tpdbd.cardpurchases.model.Purchase purchase) {
            return switch (purchase) {
                case com.tpdbd.cardpurchases.model.CashPurchase ca -> CashPurchase.fromModel(ca);
                case com.tpdbd.cardpurchases.model.CreditPurchase cr -> CreditPurchase.fromModel(cr);
                default -> throw new IllegalArgumentException("Unknow type of purchase");
            };
        }
    }

    record CashPurchase(
        long id,
        String cardNumber,
        Optional<String> PaymentVoucher,
        String store,
        String cuitStore,
        float ammount,
        float finalAmount,
        List<Quota> quotas,
        float storeDiscount)
        implements Purchase 
    {
        public static CashPurchase fromModel(com.tpdbd.cardpurchases.model.CashPurchase cashPurchase) {
            return new CashPurchase(
                cashPurchase.getId(),
                cashPurchase.getCard().getNumber(), 
                cashPurchase.getPaymentVoucher(), 
                cashPurchase.getStore(),
                cashPurchase.getCuitStore(), 
                cashPurchase.getAmount(),
                cashPurchase.getFinalAmount(),
                cashPurchase.getQuotas().stream().map(Quota::fromModel).toList(),
                cashPurchase.getStoreDiscount());
        }

        @Override
        public String getType() {
            return this.getClass().getSimpleName();
        }
    }

    record CreditPurchase(
        long id,
        String cardNumber,
        Optional<String> PaymentVoucher,
        String store,
        String cuitStore,
        float ammount,
        float finalAmount,
        List<Quota> quotas,
        float interest, 
        int numberOfQuotas)
        implements Purchase  
    {
        public static CreditPurchase fromModel(com.tpdbd.cardpurchases.model.CreditPurchase creditPurchase) {
            return new CreditPurchase(
                creditPurchase.getId(),
                creditPurchase.getCard().getNumber(), 
                creditPurchase.getPaymentVoucher(), 
                creditPurchase.getStore(),
                creditPurchase.getCuitStore(), 
                creditPurchase.getAmount(),
                creditPurchase.getFinalAmount(),
                creditPurchase.getQuotas().stream().map(Quota::fromModel).toList(),
                creditPurchase.getInterest(),
                creditPurchase.getNumberOfQuotas());
        }

        @Override
        public String getType() {
            return this.getClass().getSimpleName();
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
