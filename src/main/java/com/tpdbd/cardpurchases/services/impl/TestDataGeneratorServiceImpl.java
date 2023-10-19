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
import java.util.Map;
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
import com.tpdbd.cardpurchases.repositories.PaymentRepository;
import com.tpdbd.cardpurchases.repositories.PromotionRepository;
import com.tpdbd.cardpurchases.repositories.PurchaseRepository;
import com.tpdbd.cardpurchases.repositories.QuotaRepository;
import com.tpdbd.cardpurchases.services.TestDataGeneratorService;
import com.tpdbd.cardpurchases.util.SequenceGenerator;
import com.tpdbd.cardpurchases.util.TransactionalCaller;
import com.tpdbd.cardpurchases.util.TriFunction;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
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

/**
 * paymentSource = quotas | cashPayments
 */
record PaymentData<T>(Float totalPrice, List<T> paymentSource) {
}


@Service
public class TestDataGeneratorServiceImpl implements TestDataGeneratorService {
    @Autowired private Environment environment;
    @Autowired private BankRepository bankRepository;
    @Autowired private CardHolderRepository cardHolderRepository;
    @Autowired private CardRepository cardRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private PromotionRepository promotionRepository;
    @Autowired private PurchaseRepository<CashPurchase> cashRepository;
    @Autowired private PurchaseRepository<CreditPurchase> creditRepository;
    @Autowired private QuotaRepository quotaRespository;

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    private Random random = new Random(0); // Same seed (0) -> all is "repeatable"
    private Faker faker;

    private SequenceGenerator cuitGenerator = new SequenceGenerator("cuit");
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
    public void generateData() {
        try (var tc = new TransactionalCaller(this.entityManagerFactory.createEntityManager())) {
            tc.call((EntityManager em) -> {
                System.out.println(">> Generating stores");
                generateStores();

                System.out.println(">> Generating banks");
                generateBanks();

                System.out.println(">> Generating promotions");
                var banks = this.bankRepository.findAll();
                generatePromotions(banks, this.stores);

                System.out.println(">> Generating card holders");
                generateCardHolders();

                System.out.println(">> Generating cards");
                var cardHolders = this.cardHolderRepository.findAll();
                generateCards(banks, cardHolders, em);

                System.out.println(">> Generating cash purchases");
                var cards = this.cardRepository.findAll();
                generateCashPurchases(this.stores, cards, em);

                System.out.println(">> Generating credit purchases");
                generateCreditPurchases(this.stores, cards, em);

                System.out.println(">> Generating credit quotas");
                var creditPurchases = this.creditRepository.findAll();
                generateQuotasTo(creditPurchases);
            });

            // Payments need all previous data saved in the db
            tc.call((EntityManager em) -> {
                System.out.println(">> Generating payments");
                generatePaymentsFor(Stream.concat(
                    StreamSupport.stream(this.cashRepository.findAll().spliterator(), false),
                    StreamSupport.stream(this.creditRepository.findAll().spliterator(), false)), em);
            });

            System.out.println(">> Done");
        }
        catch (Exception ex) {
            System.err.println("Unexpected error: " + ex.getStackTrace().toString()); // TODO Add a proper logging
        }
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

    private void generateStores() {
        this.stores = this.faker.collection(() -> new Store(
                this.faker.company().name(),
                this.cuitGenerator.getNextValue())
            )
            .len(getIntParam("numOfStores"))
            .generate();
    }

    private void generateBanks() {
        List<Bank> banks = this.faker.collection(() -> new Bank(
                this.faker.company().name(),
                this.cuitGenerator.getNextValue(),
                this.faker.address().fullAddress(),
                this.faker.phoneNumber().phoneNumber())
            )
            .len(getIntParam("numOfBanks"))
            .generate();

        this.bankRepository.saveAll(banks);
    }

    private void generatePromotions(Iterable<Bank> banks, Iterable<Store> stores) {
        var promotions = new ArrayList<Promotion>();

        banks.forEach(bank -> {
            // Asumption:
            //   - Each Bank will emit both types of promotions (Discount and
            //     Financing) for each promoted Store, so at most, numOfStores * 2.
            //   - All banks use the same generator of promotion codes, so
            //     promotions across all banks have a unique code.
            getRandomItemsFrom(stores, this.faker.number().numberBetween(1, getIntParam("numOfStores")))
                .forEach(promotedStore -> {
                    var validityStart = getFakeDate();
                    var validityEnd = getFakeDate(validityStart);

                    promotions.add(new Discount(
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

                    promotions.add(new Financing(
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

        this.promotionRepository.saveAll(promotions);
    }

    private void generateCardHolders() {
        Iterable<CardHolder> cardHolders =
            this.faker.collection(() -> new CardHolder(
                this.faker.name().fullName(),
                // faker.idNumber() is not good fot DNI, it generes IDs with hyphens
                String.valueOf(this.faker.number().numberBetween(1, 99_999_999)),
                Integer.toString(this.faker.number().positive()),
                this.faker.address().fullAddress(),
                this.faker.phoneNumber().cellPhone(),
                getFakeDate())
            )
            .len(getIntParam("numOfCardHolders"))
            .generate();

        this.cardHolderRepository.saveAll(cardHolders);
    }

    /**
     * Generates cards for all card holders in some random banks
     * @param banks
     * @param cardHolders
     */
    private void generateCards(Iterable<Bank> banks, Iterable<CardHolder> cardHolders, EntityManager entityManager) {
        var cards = new ArrayList<Card>();

        cardHolders.forEach(cardHolder -> {
            var cardHolderBanks = new ArrayList<Bank>();

            getRandomItemsFrom(banks, this.faker.number().numberBetween(1, getIntParam("maxNumOfCardsPerUser")))
                .forEach(bank -> {
                    cardHolderBanks.add(bank);

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

            // For model consistency, link banks and card holders
            cardHolder.setBanks(cardHolderBanks);
            this.cardHolderRepository.save(cardHolder);
        });

        this.cardRepository.saveAll(cards);
    }

    /**
     * Generates some cash purchases for each card in some random stores
     * @param stores
     * @param cards
     */
    private void generateCashPurchases(Iterable<Store> stores, Iterable<Card> cards, EntityManager entityManager) {
        var purchases = generatePurchases(
            getIntParam("maxNumOfCashPurchasesPerCard"),
            stores,
            cards,
            entityManager,
            (store, card, promotions) -> {
                var promo = promotions.stream()
                    .filter(p -> Discount.class.isInstance(p))
                    .map(p -> Discount.class.cast(p))
                    .findAny();

                //var voucher = promo.map(Discount::getCode).orElse(null);
                var voucher = faker.simpsons().character();
                var priceCap = promo.map(Discount::getPriceCap).orElse(0.f);
                var promoDiscount = promo.map(Discount::getDiscountPercentage).orElse(0.f);
                var storeDiscount = faker.number().numberBetween(1, getIntParam("maxDiscount")) / 100.f;

                var amount = (float)this.faker.number().randomDouble(2, 1, getIntParam("maxCashPurchaseAmount"));
                var finalPromoDiscount = amount <= priceCap ? promoDiscount : 0.f;
                var finalDiscount = finalPromoDiscount + storeDiscount;
                assert finalDiscount < 1; // TODO Add a proper error management

                var finalAmount = amount * (1 - finalDiscount);

                var shopDate = getFakeDate(card.getSince());

                return new CashPurchase(
                    card,
                    voucher,
                    store.name(),
                    store.cuit(),
                    amount,
                    finalAmount,
                    storeDiscount,
                    shopDate.getMonthValue(),
                    shopDate.getYear(),
                    promo.orElse(null));
            });

        this.cashRepository.saveAll(purchases);
    }

    /**
     * Generates some credit purchases for each card in some random stores
     * @param stores
     * @param cards
     */
    private void generateCreditPurchases(Iterable<Store> stores, Iterable<Card> cards, EntityManager entityManager) {
        var purchases = generatePurchases(
            getIntParam("maxNumOfCreditPurchasesPerCard"),
            stores,
            cards,
            entityManager,
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

                var voucher = faker.simpsons().character();

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
                    voucher,
                    store.name(),
                    store.cuit(),
                    amount,
                    finalAmount,
                    finInterest,
                    finNumOfQuotas,
                    discountPromo.orElse(null));

                return purchase;
            });

        this.creditRepository.saveAll(purchases);
    }

    /**
     * Defines the purchase template algorithm, it can generates cash or credit purchases
     *
     * @param <T>
     * @param <U>
     * @param stores
     * @param cards
     * @param paymentCreator
     */
    private <T extends Purchase>
        Iterable<T> generatePurchases(int maxNumOfPurchases,
                                      Iterable<Store> stores,
                                      Iterable<Card> cards,
                                      EntityManager entityManager,
                                      TriFunction<Store, Card, List<Promotion>, T> purchaseCreator)
    {
        var purchases = new ArrayList<T>();

        if (maxNumOfPurchases <= 0)
            return purchases;

        cards.forEach(card -> {
            var numOfPurchases = this.random.nextInt(maxNumOfPurchases) + 1;
            var bank = entityManager.merge(card.getBank()); // re-attach the bank to the current context

            getRandomItemsFrom(stores, numOfPurchases)
                .forEach(store -> {
                    var promotions = bank.getPromotions().stream()
                        .filter(promo -> promo.getCuitStore().equals(store.cuit()))
                        .toList();


                    assert promotions.size() <=2; // A bank has at most 2 promotions for each store
                    purchases.add(purchaseCreator.apply(store, card, promotions));
                });
        });

        return purchases;
    }

    /**
     * Generates quotas to each credit purchase in the specified list of them
     * @param purchases
     */
    private void generateQuotasTo(Iterable<CreditPurchase> purchases)
    {
        var quotas = new ArrayList<Quota>();

        purchases.forEach(purchase -> {
            var numOfQuotas = purchase.getNumberOfQuotas();
            var shopDate = getFakeDate(purchase.getCard().getSince());
            var amountPerQuota = purchase.getFinalAmount() / numOfQuotas;

            // Adjust the number of quotas if they exceed the expiration date of the credit card
            var monthsForExpiration = ChronoUnit.MONTHS.between(purchase.getCard().getExpirationDate(), shopDate);
            var finalNumOfQuotas = numOfQuotas > monthsForExpiration ? monthsForExpiration : numOfQuotas;

            for (int i = 1; i <= finalNumOfQuotas; i++) {
                // Each quota is set to the following month of the previous one
                var paymentDate = shopDate.plusMonths(i);

                quotas.add(new Quota(
                    purchase,
                    i,
                    amountPerQuota,
                    paymentDate.getMonthValue(),
                    paymentDate.getYear()));
            }
        });

        this.quotaRespository.saveAll(quotas);
    }

    /**
     * Generates monthly payments for all purchases, grouping quotas and cash payments of the same card
     * for the same period of time (one payment for them).
     *
     * @param purchases
     * @param entityManager
     *
     * TODO This method is really nasty, it must be refactored, but, for sure, it will require a data model refactoring
     */
    private void generatePaymentsFor(Stream<Purchase> purchases, EntityManager entityManager) {
        var paymentsUntil = getDateParam("generatePaymentsUntil");

        purchases
            .map(purchase -> entityManager.merge(purchase)) // re-attach each purchae to the current context
            .collect(Collectors.groupingBy(Purchase::getCard))
            .values()
            .forEach((purchasesByCard) -> {
                var creditPaymentsPerPeriod = purchasesByCard.stream()
                    .filter(purchase -> CreditPurchase.class.isInstance(purchase))
                    .map(purchase -> CreditPurchase.class.cast(purchase))
                    .flatMap(purchase -> purchase.getQuotas().stream())
                    .filter(quota -> {
                        // Quota doesn't have the day, just use the first one
                        var quotaDate = LocalDate.of(quota.getYear(), quota.getMonth(), 1);
                        return quotaDate.isBefore(paymentsUntil);
                    })
                    .collect(Collectors.groupingBy(quota ->
                        new PaymentPeriod(quota.getMonth(), quota.getYear())))
                    .entrySet().stream()
                    // Convert Map<PaymentPeriod, List<Quota>> into Map<PaymentPeriod, Sum(price of al quotas)>
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                        var quotas = entry.getValue();
                        var totalPrice = quotas.stream()
                            .map(Quota::getPrice)
                            .reduce(0.f, (accum, price) -> accum + price);

                        return new PaymentData<Quota>(totalPrice, quotas);
                    }));

                var cashPaymentsPerPeriod = purchasesByCard.stream()
                    .filter(purchase -> CashPurchase.class.isInstance(purchase))
                    .map(purchase -> CashPurchase.class.cast(purchase))
                    .filter(purchase -> {
                        // Cash purchase doesn't have the day, just use the first one
                        var purchaseDate = LocalDate.of(purchase.getYear(), purchase.getMonth(), 1);
                        return purchaseDate.isBefore(paymentsUntil);
                    })
                    .collect(Collectors.groupingBy(purchase ->
                        new PaymentPeriod(purchase.getMonth(), purchase.getYear())))
                    .entrySet().stream()
                    // Convert Map<PaymentPeriod, List<CashPayments>> into Map<PaymentPeriod, Sum(price of cash payments)>
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                        var cashPurchases = entry.getValue();
                        var totalPrice = cashPurchases.stream()
                            .map(CashPurchase::getFinalAmount)
                            .reduce(0.f, (accum, price) -> accum + price);

                        return new PaymentData<CashPurchase>(totalPrice, cashPurchases);
                    }));

                    //
                    // Groups cash purchases and credit quotas to generate the final payment per period
                    //

                    var periodsAndTotalPrices = Stream.concat(
                        StreamSupport.stream(creditPaymentsPerPeriod.entrySet().spliterator(), false),
                        StreamSupport.stream(cashPaymentsPerPeriod.entrySet().spliterator(), false));

                    periodsAndTotalPrices
                        .collect(Collectors.groupingBy(Map.Entry::getKey)) // groups by payment period
                        .forEach((period, entries) -> {
                            var totalPaymentPrice = entries.stream()
                                .map(entry -> entry.getValue().totalPrice())
                                .reduce(0.f, (accum, price) -> accum + price);

                            // Due to groupping by card, all purchases available here were made with the same card
                            var card = purchasesByCard.get(0).getCard();

                            var payment = new Payment(
                                this.paymentCode.getNextValue(),
                                period.month(),
                                period.year(),
                                LocalDate.of(period.year(), period.month(), 15),
                                LocalDate.of(period.year(), period.month(), 25),
                                totalPaymentPrice * 0.5f, // TODO Surcharge of 5% is harcoded
                                totalPaymentPrice,
                                card);

                            final var savedPayment = this.paymentRepository.save(payment);

                            // Set the bi-directional relationship between payments and quotas and cash purchases
                            entries.forEach(entry -> {
                                var paymentData = entry.getValue();
                                paymentData.paymentSource().forEach(entity -> {
                                    switch(entity) {
                                        case Quota quota -> quota.setPayment(savedPayment);
                                        case CashPurchase purchase -> purchase.setPayment(savedPayment);
                                        default -> throw new IllegalArgumentException(
                                            "Unknown type of entity (only Quota or CashPayment allowed");

                                    }
                                });
                            });
                        });
            });
    }
}
