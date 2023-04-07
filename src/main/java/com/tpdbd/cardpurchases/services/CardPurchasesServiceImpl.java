package com.tpdbd.cardpurchases.services;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.dto.RequestDTO;
import com.tpdbd.cardpurchases.errors.BankNotFoundException;
import com.tpdbd.cardpurchases.errors.PaymentNotFoundException;
import com.tpdbd.cardpurchases.model.Card;
import com.tpdbd.cardpurchases.repositories.BankRepository;
import com.tpdbd.cardpurchases.repositories.CardRepository;
import com.tpdbd.cardpurchases.repositories.PaymentRepository;

import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;

@Service
public class CardPurchasesServiceImpl implements CardPurchasesService {
    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CardRepository cardRepository;

    @Override
    @Transactional
    public void banksAddDiscountPromotion(String cuit, RequestDTO.Discount discount) {
        var bank = this.bankRepository
                .findByCuit(cuit)
                .orElseThrow(() -> new BankNotFoundException(cuit));

        bank.addPromotion(RequestDTO.Discount.toModel(discount, bank));
        this.bankRepository.save(bank);
    }

    @Override
    @Transactional
    public void paymentsUpdateDates(String code, LocalDate firstExpiration, LocalDate secondExpiration) {
        var payment = this.paymentRepository
                .findByCode(code)
                .orElseThrow(() -> new PaymentNotFoundException(code));

        payment.setFirstExpiration(firstExpiration);
        payment.setSecondExpiration(secondExpiration);

        this.paymentRepository.save(payment);
    }

    @Override
    public Set<Card> cardsGetNextExpire(@Nullable LocalDate baseDate, @Nullable Integer daysFromBaseDate) {
        var startDate = baseDate != null ? baseDate : LocalDate.now();
        var finalDate = startDate.plusDays(daysFromBaseDate != null ? daysFromBaseDate : 30);

        var cards = this.cardRepository.findByExpirationDateBetween(startDate, finalDate);

        return StreamSupport.stream(cards.spliterator(), false)
                .collect(Collectors.toSet());
    }
}
