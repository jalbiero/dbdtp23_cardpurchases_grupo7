package com.tpdbd.cardpurchases.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tpdbd.cardpurchases.model.Bank;
import com.tpdbd.cardpurchases.model.Discount;
import com.tpdbd.cardpurchases.services.CardPurchasesService;

/**
 * All endpoint paths that start with "tests" are endpoints that
 * were no asked to be created, but are necessary for unit test
 */
@RestController
class CardPurchasesController {

    @Autowired
    private CardPurchasesService service;

    @GetMapping("/")
    String index() {
        return "Card Purchases application";
    }

    @GetMapping("/tests/banks/cuits")
    Map<String, List<String>> testGetBanks() {
        var result = new LinkedHashMap<String, List<String>>();
        result.put("cuits", this.service.testsBanksGetCuits());
        return result;
    }

    @GetMapping("/tests/banks/{cuit}")
    Bank testGetBank(@PathVariable String cuit) {
        return this.service.testsBanksGetBank(cuit);
    }

    @PostMapping("/banks/{cuit}/addDiscountPromotion")
    void banksAddDiscountPromotion(@PathVariable String cuit, @RequestBody Discount discount) {
        this.service.banksAddDiscountPromotion(cuit, discount);
    }

}
