package com.tpdbd.cardpurchases.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpdbd.cardpurchases.model.Bank;
import com.tpdbd.cardpurchases.services.CardPurchasesService;

/**
 * This controller has all endpoints necessary for manual or automated testing
 */
@RestController
@RequestMapping("/tests")
public class TestController {
    @Autowired
    private CardPurchasesService service;

    @GetMapping("/")
    String index() {
        return "Card Purchases application - Test API";
    }

    @GetMapping("/banks/cuits")
    Map<String, List<String>> testGetBanks() {
        var result = new LinkedHashMap<String, List<String>>();
        result.put("cuits", this.service.testsBanksGetCuits());
        return result;
    }

    @GetMapping("/banks/{cuit}")
    Bank testGetBank(@PathVariable String cuit) {
        return this.service.testsBanksGetBank(cuit);
    }
}
