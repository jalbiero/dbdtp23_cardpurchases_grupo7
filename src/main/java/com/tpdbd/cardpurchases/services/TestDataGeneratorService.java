package com.tpdbd.cardpurchases.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.TreeSet;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.model.Bank;
import com.tpdbd.cardpurchases.model.Card;
import com.tpdbd.cardpurchases.model.CardHolder;
import com.tpdbd.cardpurchases.model.CashPayment;
import com.tpdbd.cardpurchases.model.Discount;
import com.tpdbd.cardpurchases.model.Financing;
import com.tpdbd.cardpurchases.model.MonthlyPayments;
import com.tpdbd.cardpurchases.model.Promotion;
import com.tpdbd.cardpurchases.model.Purchase;
import com.tpdbd.cardpurchases.repositories.BankRepository;
import com.tpdbd.cardpurchases.repositories.CardHolderRepository;
import com.tpdbd.cardpurchases.repositories.CardRepository;
import com.tpdbd.cardpurchases.repositories.PurchaseRepository;
import com.tpdbd.cardpurchases.util.TriFunction;

import net.datafaker.Faker;

// @formatter:off

class Sequence {
    private int value;

    public Sequence() {
        this(0);
    }

    public Sequence(int initialValue) {
        value = initialValue;
    }

    String getNextValue() {
        return Integer.toString(value++);
    }
}

record Store(String name, String cuit) {
}

@Service
public class TestDataGeneratorService {
    @Autowired BankRepository bankRepository;
    @Autowired CardHolderRepository cardHolderRepository;
    @Autowired CardRepository cardRepository;
    @Autowired PurchaseRepository<CashPayment> cashRepository; 
    @Autowired PurchaseRepository<MonthlyPayments> monthlyRepository; 

    // TODO Add these constants to property file
    public static int NUM_OF_BANKS = 10;
    public static int NUM_OF_PROMOTIONS_PER_BANK = 15;
    public static int NUM_OF_STORES = 50;
    public static int NUM_OF_CARD_HOLDERS = 50;
    public static int MAX_NUM_OF_CARDS_PER_USER = 3;
    public static int MAX_NUM_OF_PAYMENTS_PER_CARD = 15;

    private Random random = new Random(0); // all is "repeatable"
    private Faker faker; 

    private Sequence cuitGenerator = new Sequence();
    private Sequence promotionCode = new Sequence();

    public TestDataGeneratorService() {
        var locale = new Locale.Builder()
            .setLanguage("es")
            .setRegion("AR")
            .build();
        
        this.faker = new Faker(locale, this.random);
    }

    public void generateData() {
        var stores = generateStores();
        var banks = generateBanks(stores);
        var cardHolders = generateCardHolders();
        var cards = generateCards(banks, cardHolders);
        var cashPayments = generateCashPayments(stores, cards);
        var monthlyPayments = generateMonthlyPayments(stores, cards);

        bankRepository.saveAll(banks);
        cardHolderRepository.saveAll(cardHolders);
        cardRepository.saveAll(cards);
        cashRepository.saveAll(cashPayments);
        monthlyRepository.saveAll(monthlyPayments);
    }

    //
    // Internal helpers
    //

    /**
     * Get random items from a list, very simple implementation for small lists
     * @param <T>
     * @param data
     * @param numOfItems
     * @return
     */
    private <T> List<T> getRandomItemsFrom(List<T> data, int numOfItems) {
        var uniqueIndexes = new TreeSet<Integer>();
        var result = new ArrayList<T>();

        this.random.ints(0, data.size())
            .takeWhile(index -> !uniqueIndexes.contains(index))
            .forEach(index -> {
                uniqueIndexes.add(index);
                result.add(data.get(index));
            });

        return result;
    }

    private List<Store> generateStores() {
        return faker.collection(() -> new Store(
                this.faker.company().name(), 
                this.cuitGenerator.getNextValue())
            )
            .len(NUM_OF_STORES)
            .generate();
    }

    /**
     * Generates a list of Banks (and their Promotions)
     * @return
     */
    private List<Bank> generateBanks(List<Store> stores) {
        // For the sake of clearity, generates Banks first and later 
        // their promotions for each one of them
        List<Bank> banks = this.faker.collection(() -> new Bank(
                this.faker.company().name(), 
                this.cuitGenerator.getNextValue(), 
                this.faker.address().fullAddress(), 
                this.faker.phoneNumber().phoneNumber())
            )
            .len(NUM_OF_BANKS)
            .generate();

        banks.forEach(bank -> {
            // Asumption: 
            //   Each Bank will emit both type of promotion (Discount and 
            //   Financing) for each promoted Store
            getRandomItemsFrom(stores, NUM_OF_PROMOTIONS_PER_BANK)
                .forEach(promotedStore -> {
                    bank.addPromotion(new Discount(
                        this.promotionCode.getNextValue(), 
                        this.faker.company().catchPhrase(), 
                        promotedStore.name(), 
                        promotedStore.cuit(), 
                        LocalDate.now(), 
                        // valid between 1 and 6 months
                        LocalDate.now().plusMonths(this.faker.number().numberBetween(1, 6)),  
                        faker.theItCrowd().characters(),
                        // discount between 1% and 15%
                        faker.number().numberBetween(1, 15) / 100.f, 
                        // cap price betwee 5000-10000
                        faker.number().numberBetween(5_000, 10_000), 
                        true));
    
                    bank.addPromotion(new Financing(
                        this.promotionCode.getNextValue(), 
                        this.faker.company().catchPhrase(), 
                        promotedStore.name(), 
                        promotedStore.cuit(), 
                        LocalDate.now(), 
                        // valid between 1 and 6 months
                        LocalDate.now().plusMonths(this.faker.number().numberBetween(1, 6)), 
                        faker.theItCrowd().actors(),
                        // number of quotas (1 to 6)
                        this.faker.number().numberBetween(1, 6), 
                        // interest between 1% and 10%
                        this.faker.number().numberBetween(1, 10) / 100.f)); 
                });
            });

        return banks;
    }

    private List<CardHolder> generateCardHolders() {
        return this.faker.collection(() -> new CardHolder(
                this.faker.name().fullName(),
                this.faker.idNumber().valid(),
                Integer.toString(this.faker.number().positive()), 
                this.faker.address().fullAddress(), 
                this.faker.phoneNumber().cellPhone(), 
                LocalDate.of( 
                    this.faker.number().numberBetween(2000, 2015),
                    this.faker.number().numberBetween(1, 12),
                    this.faker.number().numberBetween(1, 30)))
            )
            .len(NUM_OF_CARD_HOLDERS)
            .generate();
    }

    private List<Card> generateCards(List<Bank> banks, List<CardHolder> cardHolders) {
        var cards = new ArrayList<Card>();

        cardHolders.forEach(cardHolder -> {
            getRandomItemsFrom(banks, MAX_NUM_OF_CARDS_PER_USER)
                .forEach(bank -> {
                    cards.add(new Card(
                        bank,
                        cardHolder,
                        this.faker.business().creditCardNumber(),
                        this.faker.business().securityCode(),
                        LocalDate.now(),
                        LocalDate.now().plusMonths(36)
                    ));
                });
            });

        return cards;
    }

    /**
     * Generates some cash payments for each card in some random stores
     * @param stores
     * @param cards
     * @return
     */
    private List<CashPayment> generateCashPayments(List<Store> stores, List<Card> cards) {
        return generatePayments(stores, cards, Discount.class, 
            (card, store, promo) -> {
                var voucher = promo.isPresent() ? promo.get().getCode() : null;                        
                var amount = (float)this.faker.number().randomDouble(2, 1000, 50000);
                var storeDiscount = promo.isPresent() ? promo.get().getDiscountPercentage() : 0.0f;
                var finalAmount = amount * (1 - storeDiscount);

                return new CashPayment(
                    card, 
                    voucher,
                    store.name(), 
                    store.cuit(), 
                    amount, 
                    finalAmount,
                    storeDiscount);
            });
    }

    /**
     * Generates some monthly payments for each card in some random stores
     * @param stores
     * @param cards
     * @return
     */
    private List<MonthlyPayments> generateMonthlyPayments(List<Store> stores, List<Card> cards) {
        return generatePayments(stores, cards, Financing.class, 
            (card, store, promo) -> {
                var voucher = promo.isPresent() ? promo.get().getCode() : null;                        
                var amount = (float)this.faker.number().randomDouble(2, 1000, 50000);
                var interest = promo.isPresent() ? promo.get().getInterest() : 0.f;
                var finalAmount = amount * (1 + interest); // TODO I am not sure about this
                var numOfQuotas = promo.isPresent() ? promo.get().getNumberOfQuotas(): 0;

                return new MonthlyPayments(
                    card, 
                    voucher,
                    store.name(), 
                    store.cuit(), 
                    amount, 
                    finalAmount,
                    interest,
                    numOfQuotas);
            });
    }

    /**
     * Defines the payment template algorithm, it can generates cash or monthy payments
     * @param <T>
     * @param <U>
     * @param stores
     * @param cards
     * @param promotionClass
     * @param paymentCreator
     * @return
     */
    private <T extends Purchase, U extends Promotion> 
        List<T> generatePayments(List<Store> stores, 
                                 List<Card> cards, 
                                 Class<U> promotionClass,
                                 TriFunction<Card, Store, Optional<U>, T> paymentCreator) 
    {
        var payments = new ArrayList<T>();

        cards.forEach(card -> {
            var numOfPayments = this.random.nextInt(MAX_NUM_OF_PAYMENTS_PER_CARD) + 1;

            getRandomItemsFrom(stores, numOfPayments)
                .forEach(store -> {
                    // Check if the store has a promotion for the card bank
                    var promotion = card.getBank().getPromotions().stream()
                        .filter(promo -> 
                            promo.getCuitStore() == store.cuit() &&
                            promotionClass.isInstance(promo)) 
                        .map(promo -> promotionClass.cast(promo))
                        .findAny();

                    // 'store' is necessary because 'promotion' can be empty
                    payments.add(paymentCreator.apply(card, store, promotion));
                });
        });

        return payments;
    }
}
