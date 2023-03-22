package com.tpdbd.cardpurchases.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.model.Bank;
import com.tpdbd.cardpurchases.model.Card;
import com.tpdbd.cardpurchases.model.CardHolder;
import com.tpdbd.cardpurchases.model.CashPayment;
import com.tpdbd.cardpurchases.model.Discount;
import com.tpdbd.cardpurchases.model.Financing;
import com.tpdbd.cardpurchases.model.MonthlyPayments;
import com.tpdbd.cardpurchases.repositories.BankRepository;
import com.tpdbd.cardpurchases.repositories.CardHolderRepository;
import com.tpdbd.cardpurchases.repositories.CardRepository;
import com.tpdbd.cardpurchases.repositories.PurchaseRepository;

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

    // TODO Refactor this function, is not clear enough
    private List<CashPayment> generateCashPayments(List<Store> stores, List<Card> cards) {
        var payments = new ArrayList<CashPayment>();

        cards.forEach(card -> {
            var numOfPayments = this.random.nextInt(MAX_NUM_OF_PAYMENTS_PER_CARD) + 1;

            getRandomItemsFrom(stores, numOfPayments)
                .forEach(store -> {
                    // Check if the store has a promotion for the card bank
                    var cashPromo = card.getBank().getPromotions().stream()
                        .filter(promo -> 
                            promo.getCuitStore() == store.cuit() &&
                            promo instanceof Discount) 
                        .map(promo -> (Discount) promo)
                        .findAny();
                    
                    var voucher = cashPromo.isPresent() ? cashPromo.get().getCode() : null;                        
                    var amount = (float)this.faker.number().randomDouble(2, 1000, 50000);
                    var storeDiscount = cashPromo.isPresent() ? cashPromo.get().getDiscountPercentage() : 0.0f;
                    var finalAmount = amount * (1 - storeDiscount);

                    payments.add(new CashPayment(
                        card, 
                        voucher,
                        store.name(), 
                        store.cuit(), 
                        amount, 
                        finalAmount,
                        storeDiscount));
                });
        });

        return payments;
    }

    private List<MonthlyPayments> generateMonthlyPayments(List<Store> stores, List<Card> cards) {
        // MonthlyPayments[] monthlyPayments = {
        //     new MonthlyPayments(cards.get(1), "", "Aerofly", "686868123", 1.0f, 500.0f, 0.0f, 4),
        //     new MonthlyPayments(cards.get(1), "", "Burguesas", "123756756", 1.0f, 500.0f, 0.0f, 10)
        // };

        // return Arrays.asList(monthlyPayments);

        var payments = new ArrayList<MonthlyPayments>();

        cards.forEach(card -> {
            var numOfPayments = this.random.nextInt(MAX_NUM_OF_PAYMENTS_PER_CARD) + 1;

            getRandomItemsFrom(stores, numOfPayments)
                .forEach(store -> {
                    // Check if the store has a promotion for the card bank
                    var financingPromo = card.getBank().getPromotions().stream()
                        .filter(promo -> 
                            promo.getCuitStore() == store.cuit() &&
                            promo instanceof Financing) 
                        .map(promo -> (Financing) promo)
                        .findAny();
                    
                    var voucher = financingPromo.isPresent() ? financingPromo.get().getCode() : null;                        
                    var amount = (float)this.faker.number().randomDouble(2, 1000, 50000);
                    var interest = financingPromo.isPresent() ? financingPromo.get().getInterest() : 0.f;
                    var finalAmount = amount * (1 + interest); // TODO I am not sure about this
                    var numOfQuotas = financingPromo.isPresent() ? financingPromo.get().getNumberOfQuotas(): 0;

                    payments.add(new MonthlyPayments(
                        card, 
                        voucher,
                        store.name(), 
                        store.cuit(), 
                        amount, 
                        finalAmount,
                        interest,
                        numOfQuotas));
                });
        });

        return payments;
    }

}
