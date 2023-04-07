package com.tpdbd.cardpurchases.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.dto.RequestDTO;
import com.tpdbd.cardpurchases.errors.BankNotFoundException;
import com.tpdbd.cardpurchases.errors.CardHolderNotFoundException;
import com.tpdbd.cardpurchases.errors.CardNotFoundException;
import com.tpdbd.cardpurchases.errors.PaymentNotFoundException;
import com.tpdbd.cardpurchases.model.Bank;
import com.tpdbd.cardpurchases.model.Card;
import com.tpdbd.cardpurchases.model.Payment;
import com.tpdbd.cardpurchases.repositories.BankRepository;
import com.tpdbd.cardpurchases.repositories.CardHolderRepository;
import com.tpdbd.cardpurchases.repositories.CardRepository;
import com.tpdbd.cardpurchases.repositories.PaymentRepository;

import jakarta.transaction.Transactional;

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    BankRepository bankRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    CardHolderRepository cardHolderRepository;

    @Override
    public List<String> getBankCuits() {
        return this.bankRepository.findAllCuits();
    }

    @Override
    public Bank getBank(String cuit) {
        return this.bankRepository
            .findByCuit(cuit)
            .orElseThrow(() -> new BankNotFoundException(cuit));
    }

    @Override
    public List<String> getCardNumbes() {
        return this.cardRepository.findAllNumbers();
    }

    @Override
    public Card getCard(String number) {
        return this.cardRepository
            .findByNumber(number)
            .orElseThrow(() -> new CardNotFoundException(number));
    }

    @Override
    @Transactional
    public String addCard(RequestDTO.Card card) {
        var bank = this.bankRepository
            .findByCuit(card.bankCuit())
            .orElseThrow(() -> new BankNotFoundException(card.bankCuit()));

        var cardHolder = this.cardHolderRepository
            .findByDni(card.cardHolderDni())
            .orElseThrow(() -> new CardHolderNotFoundException(card.cardHolderDni()));

        var newCard = new Card(
            bank,
            cardHolder,
            card.number(),
            card.ccv(),
            card.since(),
            card.expirationDate());

        return this.cardRepository.save(newCard).getNumber();
    }

    @Override
    public List<String> getPaymentCodes() {
        return this.paymentRepository.findAllCodes();
    }

    @Override
    public Payment getPayment(String code) {
        return this.paymentRepository
            .findByCode(code)
            .orElseThrow(() -> new PaymentNotFoundException(code));
    }
}
