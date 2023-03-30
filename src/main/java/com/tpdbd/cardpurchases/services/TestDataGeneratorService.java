package com.tpdbd.cardpurchases.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import com.tpdbd.cardpurchases.model.Quota;
import com.tpdbd.cardpurchases.repositories.BankRepository;
import com.tpdbd.cardpurchases.repositories.CardHolderRepository;
import com.tpdbd.cardpurchases.repositories.CardRepository;
import com.tpdbd.cardpurchases.repositories.PaymentRepository;
import com.tpdbd.cardpurchases.repositories.PurchaseRepository;
import com.tpdbd.cardpurchases.util.SequenceGenerator;
import com.tpdbd.cardpurchases.util.TriFunction;

import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import net.datafaker.Faker;

// @formatter:off


/**
 * Store entity
 */
record Store(String name, String cuit) {
}

/**
 * Key for groupping payments
 */
record PaymentPeriod(Integer month, Integer year) {
}

@Service
public class TestDataGeneratorService {
    @Autowired private Environment environment;
    @Autowired private BankRepository bankRepository;
    @Autowired private CardHolderRepository cardHolderRepository;
    @Autowired private CardRepository cardRepository;
    @Autowired private PurchaseRepository<CashPurchase> cashRepository; 
    @Autowired private PurchaseRepository<CreditPurchase> creditRepository; 
    @Autowired private PaymentRepository paymentRepository; 

    private Random random = new Random(0); // all is "repeatable"
    private Faker faker; 

    private SequenceGenerator cuitGenerator = new SequenceGenerator();
    private SequenceGenerator promotionCode = new SequenceGenerator("promo");
    private SequenceGenerator paymentCode = new SequenceGenerator("payment");

    public TestDataGeneratorService() {
        var locale = new Locale.Builder()
            .setLanguage("es")
            .setRegion("AR")
            .build();
        
        this.faker = new Faker(locale, this.random);
    }

    @Transactional
    public void generateData() {
        var stores = generateStores();
        var banks = generateBanks(stores);
        var cardHolders = generateCardHolders();
        var cards = generateCards(banks, cardHolders);
        var cashPurchases = generateCashPurchases(stores, cards);
        var creditPurchases = generateCreditPurchases(stores, cards);
        var payments = generatePayments2(Stream.concat(cashPurchases.stream(), creditPurchases.stream()));

        this.bankRepository.saveAll(banks);
        this.cardHolderRepository.saveAll(cardHolders);
        this.cardRepository.saveAll(cards);
        this.cashRepository.saveAll(cashPurchases);
        this.creditRepository.saveAll(creditPurchases);
        //this.paymentRepository.saveAll(payments);
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
                // TODO Add a proper logger instead of this console output
                System.err.println((
                    String.format("Invalid syntax for parameter = %s, value = %s, error = %s", 
                        param, getStrParam(param), e)));

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
     * Get random items from a list. It is a very simple implementation especially 
     * suitable for 'numOfItems' to be much smaller than data.size() (otherwise this 
     * function will be very slow, see internal todo)
     * @param <T>
     * @param data
     * @param numOfItems
     * @return
     */
    public <T> List<T> getRandomItemsFrom(List<T> data, int numOfItems) {
        var uniqueIndexes = new TreeSet<Integer>();
        var result = new ArrayList<T>();

        if (data.size() < numOfItems)
            return data;

        // TODO For numOfItems / data.size() > 0.5 the implementation
        //      should be the opposite: select the items to be removed from 'data'

        this.random.ints(0, data.size())
            .takeWhile(index -> uniqueIndexes.size() < numOfItems)
            .forEach(index -> {
                if (!uniqueIndexes.contains(index)) {
                    uniqueIndexes.add(index);
                    result.add(data.get(index));
                }
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
            //   - Each Bank will emit both types of promotions (Discount and 
            //     Financing) for each promoted Store, so at most, maxNumOfPromotionsPerBank * 2.
            //   - All banks use the same generator of promotion codes, so 
            //     promotions across all banks have a unique code.
            getRandomItemsFrom(stores, this.faker.number().numberBetween(1, getIntParam("maxNumOfPromotionsPerBank")))
                .forEach(promotedStore -> {
                    var validityStart = getFakeDate();
                    var validityEnd = getFakeDate(validityStart);

                    // TODO If 
                    bank.addPromotion(new Discount(
                        bank,
                        this.promotionCode.getNextValue(), 
                        this.faker.company().catchPhrase(), 
                        promotedStore.name(), 
                        promotedStore.cuit(), 
                        validityStart,
                        validityEnd, 
                        faker.theItCrowd().characters(),
                        faker.number().numberBetween(1, getIntParam("maxDiscount")) / 100.f, 
                        faker.number().numberBetween(getIntParam("minCapPrice"), getIntParam("maxCapPrice")), 
                        true));

                    validityStart = getFakeDate();
                    validityEnd = getFakeDate(validityStart);
    
                    bank.addPromotion(new Financing(
                        bank,
                        this.promotionCode.getNextValue(), 
                        this.faker.company().catchPhrase(), 
                        promotedStore.name(), 
                        promotedStore.cuit(), 
                        validityStart,
                        validityEnd, 
                        faker.theItCrowd().actors(),
                        this.faker.number().numberBetween(1, getIntParam("maxNumOfQuotas")), 
                        this.faker.number().numberBetween(1, getIntParam("maxInterest")) / 100.f)); 
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

                var purchase = new CashPurchase(
                    card, 
                    voucher,
                    store.name(), 
                    store.cuit(), 
                    amount, 
                    finalAmount,
                    storeDiscount);

                generateQuotasTo(purchase, 1, finalAmount);

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
                
                var purchase = new CreditPurchase(
                    card, 
                    voucher,
                    store.name(), 
                    store.cuit(), 
                    amount, 
                    finalAmount,
                    interest,
                    numOfQuotas);

                generateQuotasTo(purchase, numOfQuotas, finalAmount);

                return purchase;
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

    private void generateQuotasTo(Purchase purchase, int numOfQuotas, float finalAmount) {
        if (numOfQuotas == 0) 
            return;

        var shopDate = getFakeDate(purchase.getCard().getSince());
        var amountPerQuota = finalAmount / numOfQuotas;

        // Adjust the number of quotas if they exceed the expiration date of the credit card
        var monthsForExpiration = ChronoUnit.MONTHS.between(purchase.getCard().getExpirationDate(), shopDate);
        var finalNumOfQuotas = numOfQuotas > monthsForExpiration ? monthsForExpiration : numOfQuotas;

        for (int i=1; i <= finalNumOfQuotas; i++) {
            // Each quota is set to the following month of the previous one
            var paymentDate = shopDate.plusMonths(i);

            purchase.addQuota(new Quota(
                purchase,
                i, 
                amountPerQuota, 
                paymentDate.getMonthValue(),
                paymentDate.getYear()));
        }
    }

    /**
     * Generates payments, grouping quotas for the same card and period of time
     * @param purchases
     * @return
     */
    private List<Payment> generatePayments(Stream<Purchase> purchases) {
        var payments = new ArrayList<Payment>();

        purchases
            .collect(Collectors.groupingBy(Purchase::getCard))
            .values()
            .forEach((purchasesByCard) -> {
                purchasesByCard.stream()
                    .flatMap(purchase -> purchase.getQuotas().stream())
                    .collect(
                        Collectors.groupingBy(
                            quota -> new PaymentPeriod(quota.getMonth(), quota.getYear()), 
                            Collectors.summingDouble(Quota::getPrice)))
                    .forEach((period, totalPrice) -> {
                        payments.add(new Payment(
                            this.paymentCode.getNextValue(),
                            period.month(),
                            period.year(),
                            LocalDate.of(period.year(), period.month(), 15),
                            LocalDate.of(period.year(), period.month(), 25),
                            5.f, // TODO %5
                            totalPrice.floatValue()
                        ));
                    });
            });

        return payments;
    }

    private List<Payment> generatePayments2(Stream<Purchase> purchases) {
        var payments = new ArrayList<Payment>();

        purchases
            .collect(Collectors.groupingBy(Purchase::getCard))
            .values()
            .forEach((purchasesByCard) -> {
                purchasesByCard.stream()
                    .flatMap(purchase -> purchase.getQuotas().stream())
                    .collect(Collectors.groupingBy(quota -> 
                        new PaymentPeriod(quota.getMonth(), quota.getYear())))
                    .forEach((period, quotas) -> {
                        var totalPrice = quotas.stream()
                            .map(Quota::getPrice)
                            .reduce(0.f, (accum, price) -> accum + price);

                        var payment = new Payment(
                            this.paymentCode.getNextValue(),
                            period.month(),
                            period.year(),
                            LocalDate.of(period.year(), period.month(), 15),
                            LocalDate.of(period.year(), period.month(), 25),
                            5.f, // TODO 5%
                            totalPrice);

                        // Set the bi-directional relationship
                        // TODO Check if this is entirely necessary
                        quotas.forEach(quota -> {
                            //payment.addQuota(quota);
                            quota.setPayment(payment);
                        });

                        payments.add(payment);
                    }); 
            });

        return payments;
    }
}
