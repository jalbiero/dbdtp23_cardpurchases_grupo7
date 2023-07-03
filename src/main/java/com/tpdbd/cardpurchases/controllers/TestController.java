package com.tpdbd.cardpurchases.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpdbd.cardpurchases.dto.RequestDTO;
import com.tpdbd.cardpurchases.dto.ResponseDTO;
import com.tpdbd.cardpurchases.services.TestDataGeneratorService;
import com.tpdbd.cardpurchases.services.TestService;

/**
 * This controller has all endpoints necessary for manual or automated testing
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private TestService service;

    @Autowired
    private TestDataGeneratorService tdgService;

    @GetMapping("/")
    String index() {
        return "Card Purchases application - Test API";
    }

    ///////////////////////
    // Banks

    @GetMapping("/banks/cuits")
    Map<String, List<String>> getBankCuits() {
        var result = new LinkedHashMap<String, List<String>>();
        result.put("cuits", this.service.getBankCuits());
        return result;
    }

    @GetMapping("/banks/{cuit}")
    ResponseDTO.Bank getBank(@PathVariable String cuit) {
        return ResponseDTO.Bank.fromModel(this.service.getBank(cuit));
    }

    ///////////////////////
    // Cards

    @GetMapping("/cards/numbers")
    Map<String, List<String>> getCardNumbers() {
        var result = new LinkedHashMap<String, List<String>>();
        result.put("numbers", this.service.getCardNumbes());
        return result;
    }

    @GetMapping("/cards/{number}")
    ResponseDTO.Card getCard(@PathVariable String number) {
        return ResponseDTO.Card.fromModel(this.service.getCard(number));
    }

    /**
     * Add a new card
     * @param card
     * @return The number of the new card
     */
    @PostMapping("/cards")
    String addCard(@RequestBody RequestDTO.Card card) {
        return this.service.addCard(card);
    }

    @DeleteMapping("/cards/{number}")
    void deleteCard(@PathVariable String number) {
        this.service.deleteCard(number);
    }

    ///////////////////////
    // Card holders

    @GetMapping("/cardHolders/dnis")
    Map<String, List<String>> getCardHolderDnis() {
        var result = new LinkedHashMap<String, List<String>>();
        result.put("dnis", this.service.getCardHolderDnis());
        return result;
    }

    ///////////////////////
    // Payments

    @GetMapping("/payments/codes")
    Map<String, List<String>> getPaymentCodes() {
        var result = new LinkedHashMap<String, List<String>>();
        result.put("codes", this.service.getPaymentCodes());
        return result;
    }

    @GetMapping("/payments/{code}")
    ResponseDTO.Payment getPayment(@PathVariable String code) {
        return ResponseDTO.Payment.fromModel(this.service.getPayment(code));
    }

    ///////////////////////
    // Purchases

    @GetMapping("/purchases/ids")
    Map<String, List<Long>> getPurchaseIds() {
        var result = new LinkedHashMap<String, List<Long>>();
        result.put("ids", this.service.getPurchaseIds());
        return result;
    }

    ///////////////////////
    // Stores

    @GetMapping("/stores/cuits")
    Map<String, List<String>> getStoreCuits() {
        var result = new LinkedHashMap<String, List<String>>();
        result.put("cuits", this.tdgService.getStoreCuits());
        return result;
    }

    ///////////////////////
    // Promotions

    @GetMapping("/promotions/codes")
    Map<String, List<String>> getPromotionCodes() {
        var result = new LinkedHashMap<String, List<String>>();
        result.put("codes", this.service.getPromotionCodes());
        return result;
    }



}
