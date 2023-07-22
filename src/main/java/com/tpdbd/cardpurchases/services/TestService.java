package com.tpdbd.cardpurchases.services;

import java.util.List;

import com.tpdbd.cardpurchases.dto.RequestDTO;
import com.tpdbd.cardpurchases.model.Bank;
import com.tpdbd.cardpurchases.model.Card;
import com.tpdbd.cardpurchases.model.Payment;

/**
 * Provide all extra funcionallity needed for manual or automated testing
 */
public interface TestService {
    List<Long> getBankIds();
    Bank getBank(Long id);

    List<String> getCardNumbes();
    Card getCard(String number);
    String addCard(RequestDTO.Card card);
    void deleteCard(String number);

    List<String> getCardHolderDnis();

    List<Long> getPaymentIds();
    Payment getPayment(Long id);

    List<Long> getPurchaseIds();

    List<String> getPromotionCodes();
}
