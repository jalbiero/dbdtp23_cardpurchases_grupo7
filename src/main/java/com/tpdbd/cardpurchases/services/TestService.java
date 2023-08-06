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
    List<String> getBankIds();
    Bank getBank(String id);

    List<String> getCardIds();
    Card getCard(String id);
    String addCard(RequestDTO.Card card);
    void deleteCard(String id);

    List<String> getCardHolderIds();

    List<String> getPaymentIds();
    Payment getPayment(String id);

    List<String> getPurchaseIds();

    List<String> getPromotionCodes();
}
