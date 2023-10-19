package com.tpdbd.cardpurchases.services.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tpdbd.cardpurchases.dto.RequestDTO;
import com.tpdbd.cardpurchases.dto.ResponseDTO;
import com.tpdbd.cardpurchases.errors.BadRequestException;
import com.tpdbd.cardpurchases.errors.NotFoundException;
import com.tpdbd.cardpurchases.services.BankService;
import com.tpdbd.cardpurchases.services.CardPurchasesService;
import com.tpdbd.cardpurchases.services.CardService;
import com.tpdbd.cardpurchases.services.PaymentService;
import com.tpdbd.cardpurchases.services.PromotionService;
import com.tpdbd.cardpurchases.services.PurchaseService;
import com.tpdbd.cardpurchases.services.QuotaService;


@Service
public class CardPurchasesServiceImpl implements CardPurchasesService {
    @Autowired
    private BankService bankService;

    @Autowired
    private CardService cardService;

    @Autowired
    private QuotaService quotaService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private PurchaseService purchaseService;

    @Override
    @Transactional
    public void banksAddDiscountPromotion(long id, RequestDTO.Discount discount) {
        var bank = this.bankService.find(id);
        var promo = RequestDTO.Discount.toModel(discount, bank);

        this.promotionService.save(promo);
    }

    @Override
    @Transactional
    public void paymentsUpdateDates(long id, LocalDate firstExpiration, LocalDate secondExpiration) {
        if (firstExpiration.isAfter(secondExpiration)) {
            throw new BadRequestException(
                "Second expiration date (%s) must be grater than the first one (%s)",
                secondExpiration, firstExpiration);
        }

        var payment = this.paymentService.find(id);

        payment.setFirstExpiration(firstExpiration);
        payment.setSecondExpiration(secondExpiration);

        this.paymentService.save(payment);
    }

    @Override
    public ResponseDTO.MonthlyPayment cardsGetMonthtlyPayment(long id, int year, int month) {
        return ResponseDTO.MonthlyPayment.fromModel(
            this.paymentService.findMonthlyPayment(id, year, month));
    }

    @Override
    public List<ResponseDTO.Card> cardsGetSoonToExpire(LocalDate baseDate, Integer daysFromBaseDate) {
        return this.cardService.findSoonToExpire(baseDate, daysFromBaseDate).stream()
            .map(ResponseDTO.Card::fromModel)
            .toList();
    }

    @Override
    public ResponseDTO.Purchase purchasesGetInfo(long purchaseId) {
        return ResponseDTO.Purchase.fromModel(
            this.purchaseService.findById(purchaseId));
    }

    @Override
    public void promotionsDelete(String code) {
        this.promotionService.deleteByCode(code);
    }

    @Override
    public ResponseDTO.CreditPurchaseTotalPrice purchasesCreditGetTotalPrice(long purchaseId) {
        var purchase = this.purchaseService.findCreditTotalPrice(purchaseId);

        return new ResponseDTO.CreditPurchaseTotalPrice(purchaseId, purchase.getFinalAmount());
    }

    @Override
    public List<ResponseDTO.Promotion> storesGetAvailblePromotions(String cuitStore, LocalDate from, LocalDate to) {
        var promotions = this.promotionService.GetAvailablePromotions(cuitStore, from, to);

        return promotions.stream()
            .map(ResponseDTO.Promotion::fromModel)
            .toList();
    }

    @Override
    public List<ResponseDTO.PurchaserCardHolder> cardsGetTop10Purchasers() {
        return this.purchaseService.findTopPurchasers(10).stream()
            .map(numOfPurchasesByCard -> new ResponseDTO.PurchaserCardHolder(
                numOfPurchasesByCard.getCard().getCardHolder().getCompleteName(),
                numOfPurchasesByCard.getNumOfPurchases(),
                numOfPurchasesByCard.getCard().getNumber()))
            .toList();
    }

    @Override
    @Transactional
    public ResponseDTO.Promotion promotionsGetTheMostUsed() {
        // For debugging purposes: Set a greater value and uncomment the
        // logging code a few lines below
        final var NUM_OF_PROMOTIONS = 1;

        var mostUsedPromotions = this.purchaseService.findTheMostUsedPromotions(NUM_OF_PROMOTIONS);

        if (mostUsedPromotions.isEmpty())
            throw new NotFoundException("No promotions used in all purchases");

        // TODO Add a propper logger instead of the console output
        // mostUsedPromotions.forEach(mostUsedPromotion -> {
        //     System.out.println(
        //         "Promotion: " + mostUsedPromotion.getPromotion().getCode() +
        //         ", # of purchases: " + mostUsedPromotion.getNumOfPurchases());
        // });

        var mostUsedPromotion = mostUsedPromotions.get(0);
        return ResponseDTO.Promotion.fromModel(mostUsedPromotion.getPromotion(), mostUsedPromotion.getNumOfPurchases());
    }

    @Override
    public ResponseDTO.Store storesGetBestSeller(int year, int month) {
        var bestSellerStore = this.quotaService.getTheBestSellerStore(year, month);

        return new ResponseDTO.Store(
            bestSellerStore.getStore(),
            bestSellerStore.getCuitStore(),
            bestSellerStore.getMonthlyProfit());
    }

    @Override
    @Transactional
    public ResponseDTO.Bank banksGetTheOneWithMostPaymentValues() {
        // For debugging purposes: Set a greater value and uncomment the
        // logging code a few lines below
        final var NUM_OF_BANKS = 1;

        var mostEarnerBanks = this.paymentService.findTheMostEarnerBanks(NUM_OF_BANKS);

        // This is a rare case (practically, the database must be empty)
        if (mostEarnerBanks.isEmpty())
            throw new NotFoundException("No banks with payments from their cards");

        // TODO Add a propper logger instead of the console output
        // mostEarnerBanks.forEach(earnerBank -> {
        //     System.out.println(
        //         "Bank: " + earnerBank.getBank().getName() +
        //         ", total payment value: " + earnerBank.getTotalPaymentValue());
        // });

        var mostEarnerBank = mostEarnerBanks.get(0);

        return ResponseDTO.Bank.fromModel(mostEarnerBank.getBank(), mostEarnerBank.getTotalPaymentValue());
    }
}
