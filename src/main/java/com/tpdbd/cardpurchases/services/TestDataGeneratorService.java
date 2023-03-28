package com.tpdbd.cardpurchases.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.model.Bank;
import com.tpdbd.cardpurchases.model.Card;
import com.tpdbd.cardpurchases.model.CardHolder;
import com.tpdbd.cardpurchases.model.CashPurchase;
import com.tpdbd.cardpurchases.model.Discount;
import com.tpdbd.cardpurchases.model.Financing;
import com.tpdbd.cardpurchases.model.CreditPurchase;
import com.tpdbd.cardpurchases.model.Promotion;
import com.tpdbd.cardpurchases.model.Purchase;
import com.tpdbd.cardpurchases.model.Quota;
import com.tpdbd.cardpurchases.repositories.BankRepository;
import com.tpdbd.cardpurchases.repositories.CardHolderRepository;
import com.tpdbd.cardpurchases.repositories.CardRepository;
import com.tpdbd.cardpurchases.repositories.PaymentRepository;
import com.tpdbd.cardpurchases.repositories.PurchaseRepository;
import com.tpdbd.cardpurchases.util.TriFunction;

import jakarta.annotation.Nullable;
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
        //var cashPayments = generatePayments(cashPurchases);
        //var creditPayments = generatePayments(creditPurchases);

        this.bankRepository.saveAll(banks);
        this.cardHolderRepository.saveAll(cardHolders);
        this.cardRepository.saveAll(cards);
        this.cashRepository.saveAll(cashPurchases);
        this.creaditRepository.saveAll(creditPurchases);
        //this.paymentRepository.saveAll(cashPayments);
    }

    //
    // Internal helpers
    //

    private <T> T getParam(String name, Class<T> retType, T defValue) {
        return this.environment.getProperty(
            String.format("application.testData.%s", name), retType, defValue);
    }

    private int getIntParam(String name) {
        return getParam(name, Integer.class, 0);
    }

    private String getStrParam(String name) {
        return getParam(name, String.class, "");
    }

    /**
     * Get a fake date based on limits given by property file. This 
     * function is a wrapper over Faker.date().between(...) because the
     * library still uses the old Date class :-(
     * @param minDate Use this date instead of the one from configuration
     * @param maxDate Use this date instead of the one from configuration
     * @return
     */
    private LocalDate getFakeDate(@Nullable LocalDate minDate, @Nullable LocalDate maxDate) {
        Function<String, Date> getDate = (param) -> {
            try {
                var sdf = new SimpleDateFormat("dd/MM/yyyy");
                return sdf.parse(getStrParam(param));  
            }
            catch (ParseException e) {
                return new Date();
            }
        };

        var fromDate = minDate == null
            ? getDate.apply("minDate")
            : Date.from(minDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        var toDate = maxDate == null
            ? getDate.apply("maxDate")
            : Date.from(maxDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        return LocalDate.ofInstant(
            this.faker.date().between(fromDate, toDate).toInstant(), 
            ZoneId.systemDefault());
    }

    private LocalDate getFakeDate() {
        return getFakeDate(null, null);
    }

    private LocalDate getFakeDate(LocalDate minDate) {
        return getFakeDate(minDate, null);
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
            .len(getIntParam("numOfStores"))
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
            .len(getIntParam("numOfBanks"))
            .generate();

        banks.forEach(bank -> {
            // Asumption: 
            //   Each Bank will emit both type of promotions (Discount and 
            //   Financing) for each promoted Store
            getRandomItemsFrom(stores, getIntParam("numOfPromotionsPerBank"))
                .forEach(promotedStore -> {
                    var validityStart = getFakeDate();
                    var validityEnd = getFakeDate(validityStart);

                    bank.addPromotion(new Discount(
                        this.promotionCode.getNextValue(), 
                        this.faker.company().catchPhrase(), 
                        promotedStore.name(), 
                        promotedStore.cuit(), 
                        validityStart,
                        validityEnd, 
                        faker.theItCrowd().characters(),
                        // discount between 1% and 15%
                        faker.number().numberBetween(1, 15) / 100.f, 
                        // cap price between 5000-10000
                        faker.number().numberBetween(5_000, 10_000), 
                        true));

                    validityStart = getFakeDate();
                    validityEnd = getFakeDate(validityStart);
    
                    bank.addPromotion(new Financing(
                        this.promotionCode.getNextValue(), 
                        this.faker.company().catchPhrase(), 
                        promotedStore.name(), 
                        promotedStore.cuit(), 
                        validityStart,
                        validityEnd, 
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
                getFakeDate())
            )
            .len(getIntParam("numOfCardHolders"))
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
            getRandomItemsFrom(banks, getIntParam("maxNumOfCardsPerUser"))
                .forEach(bank -> {
                    var since = getFakeDate(cardHolder.getEntry());
                    var expiration = this.faker.number().numberBetween(1, getIntParam("maxCardExpirationYears"));

                    cards.add(new Card(
                        bank,
                        cardHolder,
                        this.faker.business().creditCardNumber(),
                        this.faker.business().securityCode(),
                        since,
                        since.plusYears(expiration)
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
        return generatePurchases(
            stores, 
            cards, 
            Discount.class, 
            (store, card, promo) -> {
                var voucher = promo.map(Discount::getCode).orElse(null);
                var priceCap = promo.map(Discount::getPriceCap).orElse(0.f);                       
                var storeDiscount = promo.map(Discount::getDiscountPercentage).orElse(0.f);
                // TODO Discount::isOnlyCash, see comments about its underlying attribute

                var amount = (float)this.faker.number().randomDouble(2, 1000, 50000);
                var finalDiscount = amount <= priceCap ? storeDiscount : 0.f;
                var finalAmount = amount * (1 - finalDiscount);

                var purchaseDate = getFakeDate(card.getSince());
                var paymentDate = purchaseDate.plusMonths(1);

                var quota = new Quota(
                    1, 
                    finalAmount, 
                    paymentDate.getMonthValue(),
                    paymentDate.getYear());

                var purchase = new CashPurchase(
                    card, 
                    voucher,
                    store.name(), 
                    store.cuit(), 
                    amount, 
                    finalAmount,
                    storeDiscount,
                    quota);

                 return purchase;
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
                var interest = promo.map(Financing::getInterest).orElse(0.f);
                var numOfQuotas = promo.map(Financing::getNumberOfQuotas).orElse(0);

                var amount = (float)this.faker.number().randomDouble(2, 1000, 50000);

                // TODO I am not sure about this:
                //    1- does interest depends on the number of quotas?
                //    2- Is is just a plain interest aplicable to amount? (like below)
                var finalAmount = amount * (1 + interest); 
                
                // The number of quotas must be less than months(card.getExpiration() - card.getSince())
                var quotas = generateQuotas(numOfQuotas, card.getSince(), finalAmount);

                return new CreditPurchase(
                    card, 
                    voucher,
                    store.name(), 
                    store.cuit(), 
                    amount, 
                    finalAmount,
                    interest,
                    quotas);
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
            var numOfPurchases = this.random.nextInt(getIntParam("maxNumOfPurchasesPerCard")) + 1;

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

    private Set<Quota> generateQuotas(int numOfQuotas, LocalDate since, float finalAmount) {
        var quotas = new LinkedHashSet<Quota>();

        if (numOfQuotas == 0) 
            return quotas;

        var shopDate = getFakeDate(since);
        var amountPerQuota = finalAmount / numOfQuotas;

        for (int i=1; i <= numOfQuotas; i++) {
            // Each quota is set to the following month of the previous one
            var quotaDate = shopDate.plusMonths(i);

            quotas.add(new Quota(
                i, 
                amountPerQuota, 
                quotaDate.getMonthValue(),
                quotaDate.getYear()));
        }

        return quotas;
    }
}
