package com.tpdbd.cardpurchases.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tpdbd.cardpurchases.model.Discount;
import com.tpdbd.cardpurchases.services.CardPurchasesService;

@RestController
class CardPurchasesController {

    @Autowired
    private CardPurchasesService service;

    @GetMapping("/")
    String index() {
        return "Card Purchases application";
    }

    @PostMapping("/bank/{bankCuit}/addDiscountPromotion")
    void addDiscountPromotion(@PathVariable String bankCuit, @RequestBody Discount discount) {
        service.addDiscountPromotion(bankCuit, discount);
    }
}
