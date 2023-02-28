package com.tpdbd.cardpurchases.services;

import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.model.Bank;
import com.tpdbd.cardpurchases.model.Card;
import com.tpdbd.cardpurchases.model.CardHolder;
import com.tpdbd.cardpurchases.model.CashPayment;
import com.tpdbd.cardpurchases.model.MonthlyPayments;
import com.tpdbd.cardpurchases.repositories.BankRepository;
import com.tpdbd.cardpurchases.repositories.CardHolderRepository;
import com.tpdbd.cardpurchases.repositories.CardRepository;
import com.tpdbd.cardpurchases.repositories.PurchaseRepository;

// @formatter:off

@Service
public class TestDataGeneratorService {
    @Autowired BankRepository bankRepository;
    @Autowired CardHolderRepository cardHolderRepository;
    @Autowired CardRepository cardRepository;
    @Autowired PurchaseRepository<CashPayment> cashRepository; 
    @Autowired PurchaseRepository<MonthlyPayments> monthlyRepository; 
 
    public void generateData() {
        Bank[] banks = {
            new Bank("ICBC", "cuit", "address", "phone"),
            new Bank("Macro", "cuit", "address", "phone"),
            new Bank("Nación", "cuit", "address", "phone"),
            new Bank("HSBC", "cuit", "address", "phone"),
            new Bank("Galicia", "cuit", "address", "phone"),
            new Bank("Citibank", "cuit", "address", "phone"),
        };

        CardHolder[] cardHolders = {
            new CardHolder("Juan Perez", "20000000", "21-20000000-8", "Avenida de mayo 10", "+541145678900", LocalDate.of(2010, 1, 20)),
            new CardHolder("María Martínez",  "22000000", "21-22000000-8", "9 de julio 15", "+541147778900", LocalDate.of(2015, 4, 20)),
            new CardHolder("Pedro Fernández", "34200023", "24-34200023-5", "Santa Fé 1530", "+541145646320", LocalDate.of(2014, 2, 25))
        };

        Card[] cards = {
            new Card(banks[0], cardHolders[0], "0012012300123", "123", LocalDate.now(), LocalDate.now().plusMonths(36)),
            new Card(banks[1], cardHolders[0], "0012012312313", "999", LocalDate.now(), LocalDate.now().plusMonths(12)),
            new Card(banks[0], cardHolders[1], "0067868682313", "432", LocalDate.now(), LocalDate.now().plusMonths(24)),
            new Card(banks[1], cardHolders[2], "0012016868683", "967", LocalDate.now(), LocalDate.now().plusMonths(12)),
        };

        CashPayment[] cashPayments = {
            new CashPayment(cards[0], "", "Zapas Store", "123123123", 1.0f, 500.0f, 0.0f),
            new CashPayment(cards[0], "", "Burguesas", "118686783", 1.0f, 500.0f, 0.0f)
        };

        MonthlyPayments[] monthlyPayments = {
            new MonthlyPayments(cards[1], "", "Aerofly", "686868123", 1.0f, 500.0f, 0.0f, 4),
            new MonthlyPayments(cards[1], "", "Burguesas", "123756756", 1.0f, 500.0f, 0.0f, 10)
        };

        bankRepository.saveAll(Arrays.asList(banks));
        cardHolderRepository.saveAll(Arrays.asList(cardHolders));
        cardRepository.saveAll(Arrays.asList(cards));
        cashRepository.saveAll(Arrays.asList(cashPayments));
        monthlyRepository.saveAll(Arrays.asList(monthlyPayments));
    }
}
