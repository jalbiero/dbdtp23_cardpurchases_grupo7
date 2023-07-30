package com.tpdbd.cardpurchases.services;

import java.util.List;

import com.tpdbd.cardpurchases.model.Bank;

public interface BankService {
    Bank find(Long id);
    
    List<Long> findAllIds(); 
}