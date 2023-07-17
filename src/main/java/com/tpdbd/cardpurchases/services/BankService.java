package com.tpdbd.cardpurchases.services;

import com.tpdbd.cardpurchases.dto.RequestDTO;

public interface BankService {
    void addDiscountPromotion(String cuit, RequestDTO.Discount discount);
}
