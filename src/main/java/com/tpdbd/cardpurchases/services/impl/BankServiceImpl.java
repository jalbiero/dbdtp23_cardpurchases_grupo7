package com.tpdbd.cardpurchases.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.errors.BankNotFoundException;
import com.tpdbd.cardpurchases.model.Bank;
import com.tpdbd.cardpurchases.repositories.BankRepository;
import com.tpdbd.cardpurchases.services.BankService;
import com.tpdbd.cardpurchases.util.StreamHelpers;

@Service
public class BankServiceImpl implements BankService {
    @Autowired
    private BankRepository bankRepository;

    @Override
    public List<String> findAllIds() {
        return StreamHelpers.toStream(this.bankRepository.findAll())
            .map(bank -> bank.getId())
            .toList();
    }

    @Override
    public Bank find(String id) {
        return this.bankRepository
            .findById(id)
            .orElseThrow(() -> new BankNotFoundException(id));
    }
}
