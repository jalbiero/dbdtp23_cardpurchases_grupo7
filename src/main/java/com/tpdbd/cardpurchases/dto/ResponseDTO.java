package com.tpdbd.cardpurchases.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Used to ignore Optional.empty fields when serialize them to json
import com.fasterxml.jackson.annotation.JsonInclude;


// This interface is a sort of namespace in order to group a set of DTO types
public interface ResponseDTO {

    ////////////////////////////////////////////////////////
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    record Bank(
        String id,
        String name,
        String cuit,
        String address,
        String telephone,
        List<Promotion> promotions,
        Optional<Float> totalPaymentValueFromItsCards)
    {
        public static Bank fromModel(com.tpdbd.cardpurchases.model.Bank bank) {
            return fromModel(bank, Optional.empty());
        }

        public static Bank fromModel(com.tpdbd.cardpurchases.model.Bank bank,
                                     float totalPaymentValueFromItsCards)
        {
            return fromModel(bank, Optional.of(totalPaymentValueFromItsCards));
        }

        private static Bank fromModel(com.tpdbd.cardpurchases.model.Bank bank,
                                      Optional<Float> totalPaymentValueFromItsCards)
        {
            return new Bank(
                bank.getId(),
                bank.getName(),
                bank.getCuit(),
                bank.getAddress(),
                bank.getTelephone(),
                bank.getPromotions().stream()
                    .map(Promotion::fromModel)
                    .toList(),
                totalPaymentValueFromItsCards);
        }
    }

    ////////////////////////////////////////////////////////
    record Card(
        String id,
        String number,
        String ccv,
        String cardholderNameInCard,
        LocalDate since,
        LocalDate expirationDate,
        String bankId, // In real life part of the card number identifies the bank
        String cardHolderId) // In real life this may not be necessary, the name provides a partial identification
    {
        public static Card fromModel(com.tpdbd.cardpurchases.model.Card card) {
            return new Card(
                card.getId(),
                card.getNumber(),
                card.getCcv(),
                card.getCardholderNameInCard(),
                card.getSince(),
                card.getExpirationDate(),
                card.getBank().getId(),
                card.getCardHolder().getId());
        }
    }

    ////////////////////////////////////////////////////////
    record Payment(
        String id,
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
                payment.getId(),
                payment.getCode(),
                payment.getMonth(),
                payment.getYear(),
                payment.getFirstExpiration(),
                payment.getSecondExpiration(),
                payment.getSurcharge(),
                payment.getTotalPrice(),
                payment.getQuotas().stream()
                    .map(Quota::fromModel)
                    .toList());
        }
    }

    ////////////////////////////////////////////////////////
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    interface Promotion {
        String getType();

        static Promotion fromModel(com.tpdbd.cardpurchases.model.Promotion promotion) {
            return fromModel(promotion, Optional.empty());
        }

        static Promotion fromModel(com.tpdbd.cardpurchases.model.Promotion promotion, int numOfPurchases) {
            return fromModel(promotion, Optional.of(numOfPurchases));
        }

        private static Promotion fromModel(com.tpdbd.cardpurchases.model.Promotion promotion, Optional<Integer> numOfPurchases) {
            return switch (promotion) {
                case com.tpdbd.cardpurchases.model.Discount d -> Discount.fromModel(d, numOfPurchases);
                case com.tpdbd.cardpurchases.model.Financing f -> Financing.fromModel(f, numOfPurchases);
                default -> throw new IllegalArgumentException("Unknown type of promotion");
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
        float priceCap,
        Optional<Integer> numOfPurchases)
        implements Promotion
    {
        public static Discount fromModel(com.tpdbd.cardpurchases.model.Discount discount, Optional<Integer> numOfPurchases) {
            return new Discount(
                discount.getCode(),
                discount.getPromotionTitle(),
                discount.getNameStore(),
                discount.getCuitStore(),
                discount.getValidityStartDate(),
                discount.getValidityEndDate(),
                discount.getComments(),
                discount.getDiscountPercentage(),
                discount.getPriceCap(),
                numOfPurchases);
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
        float interest,
        Optional<Integer> numOfPurchases)
        implements Promotion
    {
        public static Financing fromModel(com.tpdbd.cardpurchases.model.Financing financing, Optional<Integer> numOfPurchases) {
            return new Financing(
                financing.getCode(),
                financing.getPromotionTitle(),
                financing.getNameStore(),
                financing.getCuitStore(),
                financing.getValidityStartDate(),
                financing.getValidityEndDate(),
                financing.getComments(),
                financing.getNumberOfQuotas(),
                financing.getInterest(),
                numOfPurchases);
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
                default -> throw new IllegalArgumentException("Unknown type of purchase");
            };
        }
    }

    record CashPurchase(
        String id,
        String cardNumber,
        String PaymentVoucher,
        String store,
        String cuitStore,
        float ammount,
        float finalAmount,
        float storeDiscount,
        int month,
        int year)
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
                cashPurchase.getStoreDiscount(),
                cashPurchase.getMonth(),
                cashPurchase.getYear());
        }

        @Override
        public String getType() {
            return this.getClass().getSimpleName();
        }
    }

    record CreditPurchase(
        String id,
        String cardNumber,
        String PaymentVoucher,
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

    ////////////////////////////////////////////////////////
    record Store(
        String name,
        String cuit,
        float profit)
    {}

    ////////////////////////////////////////////////////////
    record MonthlyPayment(
        String cardId,
        String cardNumber,
        int year,
        int month,
        float totalPrice,
        List<Purchase> purchases)
    {
        public static MonthlyPayment fromModel(com.tpdbd.cardpurchases.model.Payment payment) {
            List<Purchase> purchases = payment.getQuotas().stream()
                .map(quota -> Purchase.fromModel(quota.getPurchase()))
                .collect(Collectors.toList());

            // TODO: It is not clear how "Payment.surcharge", "Payment.firstExpiration" and
            //       "Payment.secondExpiration" must be used. Is the Payment class a "payment order"
            //       or a "payment record"? Sometimes seems to be the 1st one, sometimes the 2nd one.
            //       Having said that, and for the sake of simplicity, I will ignore the "Payment.surcharge"
            //       using only the "Payment.totalPrice" to represents the amount of money that
            //       a Card holder has payed in the given period (year and month)

            return new MonthlyPayment(
                payment.getCard().getId(),
                payment.getCard().getNumber(),
                payment.getYear(),
                payment.getMonth(),
                payment.getTotalPrice(),
                purchases
            );
        }
    }

    ////////////////////////////////////////////////////////
    record PurchaserCardHolder(
        String cardHolderName,
        int numOfPurchases,
        String cardNumber)
    {}

    ////////////////////////////////////////////////////////
    record CreditPurchaseTotalPrice(
        String id,
        float totalPrice)
    {}

    ////////////////////////////////////////////////////////
    record Id(
        String id)
    {}

    ////////////////////////////////////////////////////////
    record Ids(
        List<String> ids)
    {}

    ////////////////////////////////////////////////////////
    record Cuits(
        List<String> cuits)
    {}

    ////////////////////////////////////////////////////////
    record PromoCodes(
        List<String> codes)
    {}
}
