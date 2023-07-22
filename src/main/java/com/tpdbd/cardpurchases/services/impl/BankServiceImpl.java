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
    public void addDiscountPromotion(Long id, RequestDTO.Discount discount) {
        var bank = find(id);

        bank.addPromotion(RequestDTO.Discount.toModel(discount, bank));
        this.bankRepository.save(bank);
    }

    @Override
    public List<Long> findAllIds() {
        return this.bankRepository.findAllIds();
        
    }

    @Override
    public Bank find(Long id) {
        return this.bankRepository
            .findById(id)
            .orElseThrow(() -> new BankNotFoundException(id));
    }
}
