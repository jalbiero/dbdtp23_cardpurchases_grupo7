package com.tpdbd.cardpurchases.services.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
import com.tpdbd.cardpurchases.repositories.PurchaseRepository;
import com.tpdbd.cardpurchases.services.TestDataGeneratorService;
import com.tpdbd.cardpurchases.util.SequenceGenerator;
import com.tpdbd.cardpurchases.util.TriFunction;

import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import net.datafaker.Faker;

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
public class TestDataGeneratorServiceImpl implements TestDataGeneratorService {
    @Autowired private Environment environment;
    @Autowired private BankRepository bankRepository;
    @Autowired private CardHolderRepository cardHolderRepository;
    @Autowired private CardRepository cardRepository;
    @Autowired private PurchaseRepository<CashPurchase> cashRepository; 
    @Autowired private PurchaseRepository<CreditPurchase> creditRepository; 

    private Random random = new Random(0); // Same seed (0) -> all is "repeatable"
    private Faker faker; 

    private SequenceGenerator cuitGenerator = new SequenceGenerator();
    private SequenceGenerator promotionCode = new SequenceGenerator("promo");
    private SequenceGenerator paymentCode = new SequenceGenerator("payment");
    private Iterable<Store> stores;

    public TestDataGeneratorServiceImpl() {
        var locale = new Locale.Builder()
            .setLanguage("es")
            .setRegion("AR")
            .build();
        
        this.faker = new Faker(locale, this.random);
    }

    @Override
    @Transactional
    public void generateData() {
        this.stores = generateStores();

        var banks = this.bankRepository.saveAll(generateBanksAndPromotions(this.stores));
        var cardHolders = this.cardHolderRepository.saveAll(generateCardHolders());
        var cards = this.cardRepository.saveAll(generateCards(banks, cardHolders));

        var cashPurchases = generateCashPurchases(this.stores, cards);
        var creditPurchases = generateCreditPurchases(this.stores, cards);

        generatePaymentsFor(Stream.concat(
            StreamSupport.stream(cashPurchases.spliterator(), false),
            StreamSupport.stream(creditPurchases.spliterator(), false)));

        this.cashRepository.saveAll(cashPurchases);
        this.creditRepository.saveAll(creditPurchases);
    }

    @Override
    public List<String> getStoreCuits() {
        return StreamSupport.stream(this.stores.spliterator() , false)
            .map(store -> store.cuit())
            .collect(Collectors.toList());
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

    private LocalDate getDateParam(String name) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(getStrParam(name), formatter);
        }
        catch (DateTimeParseException e) {
            // TODO Add a proper logger instead of this console output
            System.err.println((
                String.format("Invalid syntax for parameter = %s, value = %s, error = %s", 
                    name, getStrParam(name), e)));

            return LocalDate.now();
        }
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
        Function<LocalDate, Date> localDate2Date = (value) ->
            Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant());

        var fromDate = localDate2Date.apply(minDate != null ? minDate : getDateParam("minDate"));
        var toDate = localDate2Date.apply(maxDate != null ? maxDate : getDateParam("maxDate")); 

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
     * Gets random items from an iterable. It is a very simple implementation especially 
     * suitable for 'numOfItems' to be much smaller than data.size() (otherwise this 
     * function will be very slow, see internal TODO).
     * @param <T>
     * @param data
     * @param numOfItems
     * @return
     */
    private <T> Iterable<T> getRandomItemsFrom(Iterable<T> data, int numOfItems) {
        // This is not ideal, but necessary in order to pickup a random item
        var randomAccessData = StreamSupport.stream(data.spliterator(), false)
            .collect(Collectors.toCollection(ArrayList::new));

        var uniqueIndexes = new TreeSet<Integer>();
        var result = new ArrayList<T>();
        
        if (randomAccessData.size() < numOfItems)
            return data;

        // TODO For numOfItems / data.size() > 0.5, the implementation
        //      should be the opposite: select the items to be removed from 'data'

        this.random.ints(0, randomAccessData.size())
            .takeWhile(index -> uniqueIndexes.size() < numOfItems)
            .forEach(index -> {
                if (!uniqueIndexes.contains(index)) {
                    uniqueIndexes.add(index);
                    result.add(randomAccessData.get(index));
                }
            });

        return result;
    }

    /**
     * Generates a random list of stores where card holders can shop
     * @return
     */
    private Iterable<Store> generateStores() {        
        return this.faker.collection(() -> new Store(
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
    private Iterable<Bank> generateBanksAndPromotions(Iterable<Store> stores) {        
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
                        faker.bool().bool()));

                    validityStart = getFakeDate();
                    validityEnd = getFakeDate(validityStart);
    
                    var numOfQuotas = this.faker.number().numberBetween(1, getIntParam("maxPromoNumOfQuotas"));
                    
                    var interest = numOfQuotas == 1 
                        ? 0.f 
                        : this.faker.number().numberBetween(1, getIntParam("maxPromoInterest")) / 100.f;

                    bank.addPromotion(new Financing(
                        bank,
                        this.promotionCode.getNextValue(), 
                        this.faker.company().catchPhrase(), 
                        promotedStore.name(), 
                        promotedStore.cuit(), 
                        validityStart,
                        validityEnd, 
                        faker.theItCrowd().actors(),
                        numOfQuotas, 
                        interest)); 
                });
            });

        return banks;
    }

    /**
     * Generates a random list of card holders
     * @return
     */
    private Iterable<CardHolder> generateCardHolders() {
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
    private Iterable<Card> generateCards(Iterable<Bank> banks, Iterable<CardHolder> cardHolders) {        
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
    private Iterable<CashPurchase> generateCashPurchases(Iterable<Store> stores, Iterable<Card> cards) {        
        return generatePurchases(
            getIntParam("maxNumOfCashPurchasesPerCard"),
            stores, 
            cards, 
            (store, card, promotions) -> {
                var promo = promotions.stream()
                    .filter(p -> Discount.class.isInstance(p))
                    .map(p -> Discount.class.cast(p))
                    .findAny();

                var voucher = promo.map(Discount::getCode).orElse(null);
                var priceCap = promo.map(Discount::getPriceCap).orElse(0.f);                       
                var promoDiscount = promo.map(Discount::getDiscountPercentage).orElse(0.f);
                var storeDiscount = faker.number().numberBetween(1, getIntParam("maxDiscount")) / 100.f;

                var amount = (float)this.faker.number().randomDouble(2, 1, getIntParam("maxCashPurchaseAmount"));
                var finalPromoDiscount = amount <= priceCap ? promoDiscount : 0.f;
                var finalDiscount = finalPromoDiscount + storeDiscount;
                assert finalDiscount < 1; // TODO Add a proper error management

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
     * 
     * @param stores
     * @param cards
     * @return
     */
    private Iterable<CreditPurchase> generateCreditPurchases(Iterable<Store> stores, Iterable<Card> cards) {
        return generatePurchases(
            getIntParam("maxNumOfCreditPurchasesPerCard"), 
            stores, 
            cards, 
            (store, card, promotions) -> {
                // TODO I am not sure about the following way of calculating the 'finalAmount' (1st apply the 
                //      discount promotion to get 'disAmount', and 2nd apply the financial promotion interest
                //      rate on the 'disAmount' in order to calculte the 'finalAmount')
                //
                //      Note: The following paragraph from 'TP-Final-202223.pdf' is confusing to me:
                //
                //       "En caso de existir más de una posible descuento para una misma compra, estos 
                //        pueden acumularse, exceptuando el caso donde existen diferentes opciones en cuotas,
                //        solo una aplicable, y cuando el descuento especi que que es solo al contado"

                var amount = (float)this.faker.number().randomDouble(2, 1, getIntParam("maxCreditPurchaseAmount"));

                ///////////////////////////////////
                // Discount part

                var discountPromo = promotions.stream()
                    .filter(p -> Discount.class.isInstance(p))
                    .map(p -> Discount.class.cast(p))
                    .filter(p -> !p.isOnlyCash())
                    .findAny();  

                var disPriceCap = discountPromo.map(Discount::getPriceCap).orElse(0.f);  
                var disPromoDiscount = discountPromo.map(Discount::getDiscountPercentage).orElse(0.f);
                var disFinalPromoDiscount = amount <= disPriceCap ? disPromoDiscount : 0.f;

                var disAmount = amount * (1 - disFinalPromoDiscount);

                ///////////////////////////////////
                // Financial part

                var financialPromo = promotions.stream()
                    .filter(p -> Financing.class.isInstance(p))
                    .map(p -> Financing.class.cast(p))
                    .findAny();

                @Nullable var finVoucher = financialPromo
                    .map(Financing::getCode)
                    .orElse(null); // No promotion
                
                var finInterest = financialPromo
                    .map(Financing::getInterest)
                    .orElse(this.faker.number().numberBetween(1, getIntParam("maxNoPromoInterest")) / 100.f);
                
                var finNumOfQuotas = financialPromo
                    .map(Financing::getNumberOfQuotas)
                    .orElse(this.faker.number().numberBetween(1, getIntParam("maxNoPromoNumOfQuotas")));

                var finalAmount = disAmount * (1 + finInterest); 

                ///////////////////////////////////
                // Purchase part

                var purchase = new CreditPurchase(
                    card, 
                    finVoucher,
                    store.name(), 
                    store.cuit(), 
                    amount, 
                    finalAmount,
                    finInterest,
                    finNumOfQuotas);

                generateQuotasTo(purchase, finNumOfQuotas, finalAmount);

                return purchase;
            });
    }

    /**
     * Defines the purchase template algorithm, it can generates cash or credit purchases
     * 
     * @param <T>
     * @param <U>
     * @param stores
     * @param cards
     * @param paymentCreator
     * @return Iterable<T>
     */
    private <T extends Purchase> 
        Iterable<T> generatePurchases(int maxNumOfPurchases,
                                      Iterable<Store> stores, 
                                      Iterable<Card> cards, 
                                      TriFunction<Store, Card, List<Promotion>, T> purchaseCreator)     
    {
        var purchases = new ArrayList<T>();

        if (maxNumOfPurchases <= 0)
            return purchases;

        cards.forEach(card -> {
            var numOfPurchases = this.random.nextInt(maxNumOfPurchases) + 1;

            getRandomItemsFrom(stores, numOfPurchases)
                .forEach(store -> {
                    var promotions = card.getBank().getPromotions().stream()
                        .filter(promo -> promo.getCuitStore() == store.cuit())
                        .toList();

                    assert promotions.size() <=2; // A bank has at most 2 promotions for each store

                    purchases.add(purchaseCreator.apply(store, card, promotions));
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
     * Generates monthly payments for all purchases, grouping quotas of the same card for the 
     * same period of time (one payment for them)
     * @param purchases
     */
    private void generatePaymentsFor(Stream<Purchase> purchases) {
        var paymentsUntil = getDateParam("generatePaymentsUntil");

        purchases
            .collect(Collectors.groupingBy(Purchase::getCard))
            .values()
            .forEach((purchasesByCard) -> {
                purchasesByCard.stream()
                    .flatMap(purchase -> purchase.getQuotas().stream())
                    .filter(quota -> {
                        // Quota doesn't have the day, just use the first one    
                        var quotaDate = LocalDate.of(quota.getYear(), quota.getMonth(), 1);
                        return quotaDate.isBefore(paymentsUntil);
                    })
                    .collect(Collectors.groupingBy(quota -> 
                        new PaymentPeriod(quota.getMonth(), quota.getYear())))
                    .forEach((period, quotas) -> {
                        var totalPrice = quotas.stream()
                            .map(Quota::getPrice)
                            .reduce(0.f, (accum, price) -> accum + price);

                        // All quotas belong to purchases made with the same card,
                        // so just use one of them to get the card
                        var card = quotas.get(0).getPurchase().getCard();

                        var payment = new Payment(
                            this.paymentCode.getNextValue(),
                            period.month(),
                            period.year(),
                            LocalDate.of(period.year(), period.month(), 15),
                            LocalDate.of(period.year(), period.month(), 25),
                            totalPrice * 0.5f, // TODO Surcharfe of 5% is harcoded
                            totalPrice,
                            card);

                        // Set the bi-directional relationship
                        // TODO Check if this is entirely necessary
                        quotas.forEach(quota -> {
                            payment.addQuota(quota);
                            quota.setPayment(payment);
                        });
                    }); 
            });
    }
}
