package com.tpdbd.cardpurchases.services;

import java.util.List;

import com.tpdbd.cardpurchases.dto.RequestDTO;
import com.tpdbd.cardpurchases.model.Bank;

public interface BankService {
    void addDiscountPromotion(String cuit, RequestDTO.Discount discount);

    Bank find(String cuit);
    
    List<String> findAllCuits(); 
}