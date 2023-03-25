package com.tpdbd.cardpurchases.services;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.model.Bank;
import com.tpdbd.cardpurchases.model.Card;
import com.tpdbd.cardpurchases.model.CardHolder;
import com.tpdbd.cardpurchases.model.CashPurchase;
import com.tpdbd.cardpurchases.model.Discount;
import com.tpdbd.cardpurchases.model.Financing;
import com.tpdbd.cardpurchases.model.Payment;
import com.tpdbd.cardpurchases.model.CreditPurchase;
import com.tpdbd.cardpurchases.model.Promotion;
import com.tpdbd.cardpurchases.model.Purchase;
import com.tpdbd.cardpurchases.repositories.BankRepository;
import com.tpdbd.cardpurchases.repositories.CardHolderRepository;
import com.tpdbd.cardpurchases.repositories.CardRepository;
import com.tpdbd.cardpurchases.repositories.PaymentRepository;
import com.tpdbd.cardpurchases.repositories.PurchaseRepository;
import com.tpdbd.cardpurchases.util.TriFunction;

import net.datafaker.Faker;

// @formatter:off

/** 
 * Very simple sequence generator
 */
class Sequence {
    private int value;
    private String prefix;

    public Sequence() {
        this(0, "");
    }

    public Sequence(int initialValue) {
        this(initialValue, "");
    }

    public Sequence(String prefix) {
        this(0, prefix);
    }

    public Sequence(int initialValue, String prefix) {
        this.value = initialValue;
        this.prefix= prefix;
    }

    String getNextValue() {
        return String.format("%s%d", this.prefix, this.value++);
    }
}

/**
 * Store entity
 */
record Store(String name, String cuit) {
}

@Service
public class TestDataGeneratorService {
    @Autowired private Environment environment;
    @Autowired private BankRepository bankRepository;
    @Autowired private CardHolderRepository cardHolderRepository;
    @Autowired private CardRepository cardRepository;
    @Autowired private PurchaseRepository<CashPurchase> cashRepository; 
    @Autowired private PurchaseRepository<CreditPurchase> creaditRepository; 
    @Autowired private PaymentRepository paymentRepository; 

    private Random random = new Random(0); // all is "repeatable"
    private Faker faker; 

    private Sequence cuitGenerator = new Sequence();
    private Sequence promotionCode = new Sequence("promo");
    private Sequence paymentCode = new Sequence("payment");

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
        var cashPurchases = generateCashPurchases(stores, cards);
        var creditPurchases = generateCreditPurchases(stores, cards);

        // TODO Generation of payments is incomplete due to some doubts in the model
        var cashPayments = generatePayments(cashPurchases);
        //var creditPayments = generatePayments(creditPurchases);

        this.bankRepository.saveAll(banks);
        this.cardHolderRepository.saveAll(cardHolders);
        this.cardRepository.saveAll(cards);
        this.cashRepository.saveAll(cashPurchases);
        this.creaditRepository.saveAll(creditPurchases);
        this.paymentRepository.saveAll(cashPayments);
    }

    //
    // Internal helpers
    //

    private int getParam(String name) {
        return this.environment.getProperty(
            String.format("application.testData.%s", name), Integer.class, 0);
    }

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

    /**
     * Generates a random list of stores where card holders can shop
     * @return
     */
    private List<Store> generateStores() {
        return faker.collection(() -> new Store(
                this.faker.company().name(), 
                this.cuitGenerator.getNextValue())
            )
            .len(getParam("numOfStores"))
            .generate();
    }

    /**
     * Generates a random list of Banks (and their Promotions)
     * @return
     */
    private List<Bank> generateBanks(List<Store> stores) {
        // For the sake of clearity, it generates Banks first and later 
        // their promotions for each one of them
        List<Bank> banks = this.faker.collection(() -> new Bank(
                this.faker.company().name(), 
                this.cuitGenerator.getNextValue(), 
                this.faker.address().fullAddress(), 
                this.faker.phoneNumber().phoneNumber())
            )
            .len(getParam("numOfBanks"))
            .generate();

        banks.forEach(bank -> {
            // Asumption: 
            //   Each Bank will emit both type of promotions (Discount and 
            //   Financing) for each promoted Store
            getRandomItemsFrom(stores, getParam("numOfPromotionsPerBank"))
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

    /**
     * Generates a random list of card holders
     * @return
     */
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
            .len(getParam("numOfCardHolders"))
            .generate();
    }

    /**
     * Generates cards for all card holders in some random banks
     * @param banks
     * @param cardHolders
     * @return
     */
    private List<Card> generateCards(List<Bank> banks, List<CardHolder> cardHolders) {
        var cards = new ArrayList<Card>();

        cardHolders.forEach(cardHolder -> {
            getRandomItemsFrom(banks, getParam("maxNumOfCardsPerUser"))
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
     * Generates some cash purchases for each card in some random stores
     * @param stores
     * @param cards
     * @return
     */
    private List<CashPurchase> generateCashPurchases(List<Store> stores, List<Card> cards) {
        return generatePurchases(stores, cards, Discount.class, 
            (store, card, promo) -> {
                var voucher = promo.map(Discount::getCode).orElse(null);                        
                var amount = (float)this.faker.number().randomDouble(2, 1000, 50000);
                var storeDiscount = promo.map(Discount::getDiscountPercentage).orElse(0.0f);
                var finalAmount = amount * (1 - storeDiscount);

                return new CashPurchase(
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
     * Generates some credit purchases for each card in some random stores
     * @param stores
     * @param cards
     * @return
     */
    private List<CreditPurchase> generateCreditPurchases(List<Store> stores, List<Card> cards) {
        return generatePurchases(stores, cards, Financing.class, 
            (store, card, promo) -> {
                var voucher = promo.map(Financing::getCode).orElse(null);
                var amount = (float)this.faker.number().randomDouble(2, 1000, 50000);
                var interest = promo.map(Financing::getInterest).orElse(0.f);
                var finalAmount = amount * (1 + interest); // TODO I am not sure about this
                var numOfQuotas = promo.map(Financing::getNumberOfQuotas).orElse(0);

                return new CreditPurchase(
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
     * Defines the purchase template algorithm, it can generates cash or credit purchases
     * @param <T>
     * @param <U>
     * @param stores
     * @param cards
     * @param promotionClass
     * @param paymentCreator
     * @return
     */
    private <T extends Purchase, U extends Promotion> 
        List<T> generatePurchases(List<Store> stores, 
                                  List<Card> cards, 
                                  Class<U> promotionClass,
                                  TriFunction<Store, Card, Optional<U>, T> purchaseCreator) 
    {
        var purchases = new ArrayList<T>();

        cards.forEach(card -> {
            var numOfPurchases = this.random.nextInt(getParam("maxNumOfPurchasesPerCard")) + 1;

            getRandomItemsFrom(stores, numOfPurchases)
                .forEach(store -> {
                    // Check if the store has a promotion for the card bank
                    var promotion = card.getBank().getPromotions().stream()
                        .filter(promo -> 
                            promo.getCuitStore() == store.cuit() &&
                            promotionClass.isInstance(promo)) 
                        .map(promo -> promotionClass.cast(promo))
                        .findAny();

                    // 'store' is necessary because 'promotion' can be empty
                    purchases.add(purchaseCreator.apply(store, card, promotion));
                });
        });

        return purchases;
    }

    /**
     * Generates cash payments for each purchase (cash or credit)
     * @return
     */
    List<Payment> generatePayments(List<CashPurchase> cashPurchases) {
        var payments = new ArrayList<Payment>();

        // TODO Many doubts about the Payment attributes
        cashPurchases.forEach(purchase -> {
            payments.add(new Payment(
                this.paymentCode.getNextValue(), 
                Integer.toString(this.faker.number().numberBetween(1, 12)),
                Integer.toString(this.faker.number().numberBetween(2015, Year.now().getValue() -1)),
                LocalDate.now(),
                LocalDate.now(),
                0.f,
                0.f));
        });

        return payments;
    }
}
