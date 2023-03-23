package com.tpdbd.cardpurchases.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.errors.BankNotFoundException;
import com.tpdbd.cardpurchases.model.Bank;
import com.tpdbd.cardpurchases.model.Discount;
import com.tpdbd.cardpurchases.repositories.BankRepository;

import jakarta.transaction.Transactional;

@Service
public class CardPurchasesServiceImpl implements CardPurchasesService {
    @Autowired
    BankRepository bankRepository;

    @Override
    @Transactional
    public List<String> testsBanksGetCuits() {
        var cuits = new ArrayList<String>();

        this.bankRepository.findAll().forEach(bank -> {
            cuits.add(bank.getCuit());
        });

        return cuits;
    }

    @Override
    @Transactional
    public Bank testsBanksGetBank(String cuit) {
        System.out.println("testsBanksGetBank: Bank = " + cuit);
        return this.bankRepository
                .findByCuit(cuit)
                .orElseThrow(() -> new BankNotFoundException(cuit));
    }

    @Override
    @Transactional
    public void banksAddDiscountPromotion(String cuit, Discount discount) {
        System.out.println("banksAddDiscountPromotion: Bank = " + cuit);
        var bank = this.bankRepository
                .findByCuit(cuit)
                .orElseThrow(() -> new BankNotFoundException(cuit))
                .addPromotion(discount);

        this.bankRepository.save(bank);
    }
}
