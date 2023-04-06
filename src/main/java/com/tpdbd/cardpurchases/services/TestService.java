package com.tpdbd.cardpurchases.services;

import java.util.List;

import com.tpdbd.cardpurchases.model.Bank;
import com.tpdbd.cardpurchases.model.Card;
import com.tpdbd.cardpurchases.model.Payment;

/**
 * Provide all extra funcionallity needed for manual or automated testing
 */
public interface TestService {
    List<String> getBankCuits();

    Bank getBank(String cuit);

    List<String> getCardNumbes();

    Card getCard(String number);

    List<String> getPaymentCodes();

    Payment getPayment(String code);
}
