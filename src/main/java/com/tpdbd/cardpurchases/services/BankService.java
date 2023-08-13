package com.tpdbd.cardpurchases.services;

import java.util.List;

import com.tpdbd.cardpurchases.model.Bank;

public interface BankService {
    Bank find(String id);
    
    List<String> findAllIds(); 

    Bank save(Bank bank);
}