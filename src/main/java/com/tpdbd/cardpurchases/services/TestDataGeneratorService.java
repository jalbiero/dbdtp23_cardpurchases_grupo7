package com.tpdbd.cardpurchases.services;

import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.model.Bank;
import com.tpdbd.cardpurchases.model.CardHolder;
import com.tpdbd.cardpurchases.repositories.BankRepository;
import com.tpdbd.cardpurchases.repositories.CardHolderRepository;

@Service
public class TestDataGeneratorService {
    @Autowired BankRepository bankRepository;
    @Autowired CardHolderRepository cardHolderRepository;
 
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
            new CardHolder("María Martínez", "22000000", "21-22000000-8", "9 de julio 15", "+541147778900", LocalDate.of(2015, 1, 20))
        };


        bankRepository.saveAll(Arrays.asList(banks));
        cardHolderRepository.saveAll(Arrays.asList(cardHolders));
    }
}
