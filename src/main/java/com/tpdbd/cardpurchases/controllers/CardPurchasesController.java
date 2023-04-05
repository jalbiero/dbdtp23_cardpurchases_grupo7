package com.tpdbd.cardpurchases.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tpdbd.cardpurchases.controllers.util.Params;
import com.tpdbd.cardpurchases.model.Card;
import com.tpdbd.cardpurchases.services.CardPurchasesService;

import jakarta.annotation.Nullable;

@RestController
public class CardPurchasesController {
    @Autowired
    private CardPurchasesService service;

    @GetMapping("/")
    String index() {
        return "Card Purchases application";
    }

    @PostMapping("/banks/{cuit}/addDiscountPromotion")
    void banksAddDiscountPromotion(@PathVariable String cuit, @RequestBody Params.Discount discount) {
        this.service.banksAddDiscountPromotion(cuit, discount);
    }

    @PutMapping("/payments/{code}/updateDates")
    void paymentsUpdateDates(@PathVariable String code, @RequestBody Params.PaymentDates paymentDates) {
        this.service.paymentsUpdateDates(code, paymentDates.firstExpiration(), paymentDates.secondExpiration());
    }

    @GetMapping("/cards/getNextExpire")
    List<Card> cardsGetNextExpired(@Nullable @RequestBody(required = false) Params.NextExpiredCards nextExpiredCards) {
        if (nextExpiredCards != null)
            return this.service.cardsGetNextExpire(nextExpiredCards.baseDate(), nextExpiredCards.daysFromBaseDate());
        else
            return this.service.cardsGetNextExpire(null, null);
    }
}
