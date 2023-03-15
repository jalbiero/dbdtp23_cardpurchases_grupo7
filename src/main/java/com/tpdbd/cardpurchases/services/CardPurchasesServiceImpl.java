package com.tpdbd.cardpurchases.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.model.Discount;
import com.tpdbd.cardpurchases.repositories.BankRepository;

import jakarta.transaction.Transactional;

@Service
public class CardPurchasesServiceImpl implements CardPurchasesService {
    @Autowired
    BankRepository bankRepository;

    @Override
    @Transactional
    public void addDiscountPromotion(String bankCuit, Discount discount) {
        bankRepository.findByCuit(bankCuit).ifPresent(bank -> {
            bank.addPromotion(discount);
        });
    }
}
