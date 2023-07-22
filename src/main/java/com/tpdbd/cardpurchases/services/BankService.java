package com.tpdbd.cardpurchases.services;

import java.util.List;

import com.tpdbd.cardpurchases.dto.RequestDTO;
import com.tpdbd.cardpurchases.model.Bank;

public interface BankService {
    void addDiscountPromotion(Long id, RequestDTO.Discount discount);

    Bank find(Long id);
    
    List<Long> findAllIds(); 
}