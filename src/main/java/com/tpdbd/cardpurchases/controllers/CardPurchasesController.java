package com.tpdbd.cardpurchases.controllers;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tpdbd.cardpurchases.dto.RequestDTO;
import com.tpdbd.cardpurchases.dto.ResponseDTO;
import com.tpdbd.cardpurchases.services.CardPurchasesService;

@RestController
public class CardPurchasesController {
    @Autowired
    private CardPurchasesService service;

    @GetMapping("/")
    String index() {
        return "Card Purchases application";
    }

    @PostMapping("/banks/{cuit}/addDiscountPromotion")
    void banksAddDiscountPromotion(@PathVariable String cuit, @RequestBody RequestDTO.Discount discount) {
        this.service.banksAddDiscountPromotion(cuit, discount);
    }

    @PutMapping("/payments/{code}/updateDates")
    void paymentsUpdateDates(@PathVariable String code, @RequestBody RequestDTO.PaymentDates paymentDates) {
        this.service.paymentsUpdateDates(code, paymentDates.firstExpiration(), paymentDates.secondExpiration());
    }

    /**
     * List cards that expire in the following 'NextExpiredCards.daysFromBaseDate' days
     * starting from 'NextExpiredCards.baseDate'.
     *
     * @param nextExpiredCards
     * @return A list of cards
     */
    @GetMapping("/cards/getNextExpire")
    Set<ResponseDTO.Card> cardsGetNextExpired(@RequestBody(required=false) 
                                              Optional<RequestDTO.NextExpiredCards> nextExpiredCards) 
    {
        var params = nextExpiredCards.orElse(new RequestDTO.NextExpiredCards());
        var cards = this.service.cardsGetNextExpire(params.baseDate(), params.daysFromBaseDate());

        return cards.stream()
                .map(ResponseDTO.Card::fromModel)
                .collect(Collectors.toSet());
    }
}
