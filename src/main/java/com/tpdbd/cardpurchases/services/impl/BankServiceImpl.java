package com.tpdbd.cardpurchases.services.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.tpdbd.cardpurchases.dto.RequestDTO;
import com.tpdbd.cardpurchases.errors.BankNotFoundException;
import com.tpdbd.cardpurchases.repositories.BankRepository;
import com.tpdbd.cardpurchases.services.BankService;

import jakarta.transaction.Transactional;

public class BankServiceImpl implements BankService {
    @Autowired
    private BankRepository bankRepository;

    @Override
    @Transactional
    public void addDiscountPromotion(String cuit, RequestDTO.Discount discount) {
        var bank = this.bankRepository
            .findByCuit(cuit)
            .orElseThrow(() -> new BankNotFoundException(cuit));

        bank.addPromotion(RequestDTO.Discount.toModel(discount, bank));
        this.bankRepository.save(bank);
    }
}
