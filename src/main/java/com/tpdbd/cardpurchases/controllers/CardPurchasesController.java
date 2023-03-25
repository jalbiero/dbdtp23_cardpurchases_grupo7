package com.tpdbd.cardpurchases.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tpdbd.cardpurchases.controllers.util.Params;
import com.tpdbd.cardpurchases.model.Discount;
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
    void banksAddDiscountPromotion(@PathVariable String cuit, @RequestBody Discount discount) {
        this.service.banksAddDiscountPromotion(cuit, discount);
    }

    @PutMapping("/payments/{code}/updateDates")
    void paymentsUpdateDates(@PathVariable String code, @RequestBody Params.PaymentDates paymentDates) {
        System.out.println("PARAMS: " + paymentDates);
        this.service.paymentsUpdateDates(code, paymentDates.firstExpiration(), paymentDates.secondExpiration());
    }
}
