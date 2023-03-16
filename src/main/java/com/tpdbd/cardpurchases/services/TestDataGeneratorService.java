package com.tpdbd.cardpurchases.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

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

    // String getNext(String format) {
    //     return String.format(format, getNext());
    // }
}

@Service
public class TestDataGeneratorService {
    @Autowired BankRepository bankRepository;
    @Autowired CardHolderRepository cardHolderRepository;
    @Autowired CardRepository cardRepository;
    @Autowired PurchaseRepository<CashPayment> cashRepository; 
    @Autowired PurchaseRepository<MonthlyPayments> monthlyRepository; 


    public void generateData() {
        // Argentine data in a repeatable way
        var faker = new Faker(new Locale("es", "AR"), new Random(0));

        var cuitGenerator = new Sequence();
        var promotionCode = new Sequence();

        // BANKS, For the sake of clearity, add Banks first and later their promotions
        List<Bank> banks = faker.collection(
            () -> new Bank(
                faker.company().name(), 
                cuitGenerator.getNextValue(), 
                faker.address().fullAddress(), 
                faker.phoneNumber().phoneNumber())
        )
        .len(10)
        .generate();

        // PROMOTIONS
        banks.forEach(bank -> {
            List<Discount> discounts = faker.collection(
                () -> new Discount(
                    promotionCode.getNextValue(), 
                    faker.company().catchPhrase(), 
                    faker.company().name(), 
                    cuitGenerator.getNextValue(), 
                    LocalDate.now(), 
                    LocalDate.now().plusMonths(faker.number().numberBetween(1, 6)), 
                    "No comments", 
                    0.20f, 
                    1000f, 
                    true)
            )
            .len(0, 5)
            .generate();

            List<Financing> financings = faker.collection(
                () -> new Financing(
                    promotionCode.getNextValue(), 
                    faker.company().catchPhrase(), 
                    faker.company().name(), 
                    cuitGenerator.getNextValue(), 
                    LocalDate.now(), 
                    LocalDate.now().plusMonths(faker.number().numberBetween(1, 6)), 
                    "No comments", 
                    faker.number().numberBetween(1, 6), 
                    (float)faker.number().randomDouble(2, 1, 10)) // interest between 1% and 10%
            )
            .len(0, 5)
            .generate();

            discounts.forEach(promo -> bank.addPromotion(promo));
            financings.forEach(promo -> bank.addPromotion(promo));
        });

        // CARD HOLDERS
        List<CardHolder> cardHolders = faker.collection(
            () -> new CardHolder(
                faker.name().fullName(),
                faker.idNumber().valid(),
                Integer.toString(faker.number().positive()), 
                faker.address().fullAddress(), 
                faker.phoneNumber().cellPhone(), 
                LocalDate.of( 
                    faker.number().numberBetween(2000, 2015),
                    faker.number().numberBetween(1, 12),
                    faker.number().numberBetween(1, 30)))
        )
        .len(10)
        .generate();

        // TOOO Complete the following:

        Card[] cards = {
            new Card(banks.get(0), cardHolders.get(0), "0012012300123", "123", LocalDate.now(), LocalDate.now().plusMonths(36)),
            new Card(banks.get(1), cardHolders.get(0), "0012012312313", "999", LocalDate.now(), LocalDate.now().plusMonths(12)),
            new Card(banks.get(0), cardHolders.get(1), "0067868682313", "432", LocalDate.now(), LocalDate.now().plusMonths(24)),
            new Card(banks.get(1), cardHolders.get(2), "0012016868683", "967", LocalDate.now(), LocalDate.now().plusMonths(12)),
        };

        CashPayment[] cashPayments = {
            new CashPayment(cards[0], "", "Zapas Store", "123123123", 1.0f, 500.0f, 0.0f),
            new CashPayment(cards[0], "", "Burguesas", "118686783", 1.0f, 500.0f, 0.0f)
        };

        MonthlyPayments[] monthlyPayments = {
            new MonthlyPayments(cards[1], "", "Aerofly", "686868123", 1.0f, 500.0f, 0.0f, 4),
            new MonthlyPayments(cards[1], "", "Burguesas", "123756756", 1.0f, 500.0f, 0.0f, 10)
        };

        bankRepository.saveAll(banks);
        cardHolderRepository.saveAll(cardHolders);
        cardRepository.saveAll(Arrays.asList(cards));
        cashRepository.saveAll(Arrays.asList(cashPayments));
        monthlyRepository.saveAll(Arrays.asList(monthlyPayments));
    }
}
