package com.tpdbd.cardpurchases.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.dto.RequestDTO;
import com.tpdbd.cardpurchases.errors.BankNotFoundException;
import com.tpdbd.cardpurchases.errors.PaymentNotFoundException;
import com.tpdbd.cardpurchases.model.Card;
import com.tpdbd.cardpurchases.model.Promotion;
import com.tpdbd.cardpurchases.model.Purchase;
import com.tpdbd.cardpurchases.repositories.BankRepository;
import com.tpdbd.cardpurchases.repositories.CardRepository;
import com.tpdbd.cardpurchases.repositories.PaymentRepository;
import com.tpdbd.cardpurchases.repositories.PromotionRepository;
import com.tpdbd.cardpurchases.repositories.PurchaseRepository;

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

    @Autowired
    private PurchaseRepository<Purchase> purchaseRepository; 

    @Autowired
    private PromotionRepository promotionRepository;

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
    public List<Card> cardsGetSoonToExpire(LocalDate baseDate, Integer daysFromBaseDate) {
        var finalDate = baseDate.plusDays(daysFromBaseDate);
        return this.cardRepository.findByExpirationDateBetween(baseDate, finalDate);
    }

    @Override
    public List<Purchase> cardsGetPurchases(String cardNumber, @Nullable String cuitStore) {
        return cuitStore == null
            ? this.purchaseRepository.findByCardNumber(cardNumber)
            : this.purchaseRepository.findByCardNumberAndCuitStore(cardNumber, cuitStore);
    }

    @Override
    public List<Promotion> storesGetAvailblePromotions(String cuitStore, LocalDate from, LocalDate to) {
        return this.promotionRepository
          .findByCuitStoreAndValidityStartDateGreaterThanEqualAndValidityEndDateLessThanEqual(cuitStore, from, to);
    }
}
