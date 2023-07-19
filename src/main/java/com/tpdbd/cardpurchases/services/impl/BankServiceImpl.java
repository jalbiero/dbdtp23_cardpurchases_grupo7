package com.tpdbd.cardpurchases.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.dto.RequestDTO;
import com.tpdbd.cardpurchases.errors.BankNotFoundException;
import com.tpdbd.cardpurchases.model.Bank;
import com.tpdbd.cardpurchases.repositories.BankRepository;
import com.tpdbd.cardpurchases.services.BankService;

import jakarta.transaction.Transactional;

@Service
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

    @Override
    public List<String> findAllCuits() {
        return this.bankRepository.findAllCuits();
    }

    @Override
    public Bank find(String cuit) {
        return this.bankRepository
            .findByCuit(cuit)
            .orElseThrow(() -> new BankNotFoundException(cuit));
    }
}
