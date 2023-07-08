package com.tpdbd.cardpurchases.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.dto.RequestDTO;
import com.tpdbd.cardpurchases.dto.ResponseDTO;
import com.tpdbd.cardpurchases.errors.BankNotFoundException;
import com.tpdbd.cardpurchases.errors.BestSellerStoreNotFoundException;
import com.tpdbd.cardpurchases.errors.MonthlyPaymentNotFoundException;
import com.tpdbd.cardpurchases.errors.NotFoundException;
import com.tpdbd.cardpurchases.errors.PaymentNotFoundException;
import com.tpdbd.cardpurchases.errors.PromotionNotFoundException;
import com.tpdbd.cardpurchases.errors.PurchaseNotFoundException;
import com.tpdbd.cardpurchases.model.Card;
import com.tpdbd.cardpurchases.model.Payment;
import com.tpdbd.cardpurchases.model.Promotion;
import com.tpdbd.cardpurchases.model.Purchase;
import com.tpdbd.cardpurchases.repositories.BankRepository;
import com.tpdbd.cardpurchases.repositories.CardRepository;
import com.tpdbd.cardpurchases.repositories.PaymentRepository;
import com.tpdbd.cardpurchases.repositories.PromotionRepository;
import com.tpdbd.cardpurchases.repositories.PurchaseRepository;
import com.tpdbd.cardpurchases.repositories.QuotaRepository;

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

    @Autowired
    private QuotaRepository quotaRepository;

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
    public ResponseDTO.MonthlyPayment cardsGetMonthtlyPayment(String cardNumber, int year, int month) {
        return ResponseDTO.MonthlyPayment.fromModel(
            this.paymentRepository
                .findMonthlyPayment(cardNumber, year, month)
                .orElseThrow(() -> new MonthlyPaymentNotFoundException(cardNumber, year, month)));
    }
  
    @Override
    public List<ResponseDTO.Card> cardsGetSoonToExpire(LocalDate baseDate, Integer daysFromBaseDate) {
        var finalDate = baseDate.plusDays(daysFromBaseDate);
        var cards = this.cardRepository.findByExpirationDateBetween(baseDate, finalDate);
        
        return cards.stream()
            .map(ResponseDTO.Card::fromModel)
            .toList();
    }

    @Override
    public Purchase purchasesGetInfo(long id) {
        return this.purchaseRepository
            .findById(id)
            .orElseThrow(() -> new PurchaseNotFoundException(id));
    }

    @Override
    public List<ResponseDTO.Promotion> storesGetAvailblePromotions(String cuitStore, LocalDate from, LocalDate to) {
        var promotions = this.promotionRepository
            .findByCuitStoreAndValidityStartDateGreaterThanEqualAndValidityEndDateLessThanEqual(cuitStore, from, to);

        return promotions.stream()
            .map(ResponseDTO.Promotion::fromModel)
            .toList();            
    }

    @Override
    public List<ResponseDTO.PurchaserCardHolder> cardsGetTop10Purchasers() {
        var top10PurchaserCards = this.purchaseRepository.findTopPurchaserCards(PageRequest.of(0, 10));

        // This is a rare case (practically, the database must be empty)
        if (top10PurchaserCards.isEmpty())
            throw new NotFoundException("No purchases could be found for any card");

        return top10PurchaserCards.get()
            .map(numOfPurchasesByCard -> new ResponseDTO.PurchaserCardHolder(
                numOfPurchasesByCard.getCard().getCardHolder().getCompleteName(),
                numOfPurchasesByCard.getNumOfPurchases(),
                numOfPurchasesByCard.getCard().getNumber()))
            .toList();
    }

    @Override
    public ResponseDTO.Store storesGetBestSeller(int year, int month) {
        var bestSellers = this.quotaRepository.findBestSellerStores(year, month, PageRequest.of(0, 1));

        if (bestSellers.isEmpty())
            throw new BestSellerStoreNotFoundException(year, month);

        var bestSeller = bestSellers.getContent().get(0);

        return new ResponseDTO.Store(
            bestSeller.getStore(), bestSeller.getCuitStore(), bestSeller.getMonthlyProfit());
    }

    @Override
    public void promotionsDelete(String code) {
        if (this.promotionRepository.deleteByCode(code) == 0) 
            throw new PromotionNotFoundException(code);
    }

}
