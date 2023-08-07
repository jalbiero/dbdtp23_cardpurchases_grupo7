package com.tpdbd.cardpurchases.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

//import jakarta.transaction.Transactional;

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
    public List<String> getBankIds() {
        return this.bankService.findAllIds();

    }

    @Override
    public Bank getBank(String id) {
        return this.bankService.find(id);
    }

    @Override
    public List<String> getCardIds() {
        return this.cardService.findAllIds();
    }

    @Override
    public Card getCard(String id) {
        return this.cardService.find(id);
    }

    @Override
    @Transactional
    public String addCard(RequestDTO.Card card) {
        var bank = this.bankService.find(card.bankId());
        var cardHolder = this.cardHolderService.find(card.cardHolderId());

        var newCard = new Card(
            bank,
            cardHolder,
            card.number(),
            card.ccv(),
            card.since(),
            card.expirationDate());

        return this.cardService.save(newCard).getId();
    }

    @Override
    public void deleteCard(String id) {
        this.cardService.delete(id);
    }

    @Override
    public List<String> getCardHolderIds() {
        return this.cardHolderService.findAllIds();
    }

    @Override
    public List<String> getPaymentIds() {
        return this.paymentService.findAllIds();
    }

    @Override
    public Payment getPayment(String id) {
        return this.paymentService.find(id);
    }

    @Override
    public List<String> getPurchaseIds() {
        return this.purchaseService.findAllIds();
    }

    @Override
    public List<String> getPromotionCodes() {
        return this.promotionService.findAllCodes();
    }
}
