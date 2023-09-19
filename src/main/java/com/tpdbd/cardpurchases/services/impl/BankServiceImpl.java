package com.tpdbd.cardpurchases.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tpdbd.cardpurchases.errors.BankNotFoundException;
import com.tpdbd.cardpurchases.model.Bank;
import com.tpdbd.cardpurchases.repositories.BankRepository;
import com.tpdbd.cardpurchases.services.BankService;

@Service
public class BankServiceImpl implements BankService {
    @Autowired
    private BankRepository bankRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Long> findAllIds() {
        return this.bankRepository.findAllIds();
    }

    @Override
    @Transactional(readOnly = true)
    public Bank find(Long id) {
        return this.bankRepository
            .findById(id)
            .orElseThrow(() -> new BankNotFoundException(id));
    }
}
