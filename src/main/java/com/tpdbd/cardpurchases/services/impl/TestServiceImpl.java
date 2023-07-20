package com.tpdbd.cardpurchases.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.dto.RequestDTO;
import com.tpdbd.cardpurchases.model.Bank;
import com.tpdbd.cardpurchases.model.Card;
import com.tpdbd.cardpurchases.model.Payment;
import com.tpdbd.cardpurchases.services.BankService;
import com.tpdbd.cardpurchases.services.CardHolderService;
import com.tpdbd.cardpurchases.services.CardService;
import com.tpdbd.cardpurchases.services.PaymentService;
import com.tpdbd.cardpurchases.services.PromotionService;
import com.tpdbd.cardpurchases.services.PurchaseService;
import com.tpdbd.cardpurchases.services.TestService;

import jakarta.transaction.Transactional;

@Service
public class TestServiceImpl implements TestService {
    @Autowired 
    BankService bankService;

    @Autowired 
    CardService cardService;

    @Autowired 
    CardHolderService cardHolderService;

    @Autowired 
    PaymentService paymentService;

    @Autowired
    PromotionService promotionService;

    @Autowired 
    PurchaseService purchaseService;


    @Override
    public List<String> getBankCuits() {
        return this.bankService.findAllCuits();

    }

    @Override
    public Bank getBank(String cuit) {
        return this.bankService.find(cuit);
    }

    @Override
    public List<String> getCardNumbes() {
        return this.cardService.findAllNumbers();
    }

    @Override
    public Card getCard(String number) {
        return this.cardService.find(number);
    }

    @Override
    @Transactional
    public String addCard(RequestDTO.Card card) {
        var bank = this.bankService.find(card.bankCuit());
        var cardHolder = this.cardHolderService.find(card.cardHolderDni());

        var newCard = new Card(
            bank,
            cardHolder,
            card.number(),
            card.ccv(),
            card.since(),
            card.expirationDate());

        return this.cardService.save(newCard).getNumber();
    }

    @Override
    public void deleteCard(String number) {
        this.cardService.delete(number);
    }

    @Override
    public List<String> getCardHolderDnis() {
        return this.cardHolderService.findAllDnis();
    }

    @Override
    public List<String> getPaymentCodes() {
        return this.paymentService.findAllCodes();
    }

    @Override
    public Payment getPayment(String code) {
        return this.paymentService.find(code);
    }

    @Override
    public List<Long> getPurchaseIds() {
        return this.purchaseService.findAllIds();
    }

    @Override
    public List<String> getPromotionCodes() {
        return this.promotionService.findAllCodes();
    }
}
