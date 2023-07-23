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

    @GetMapping("/banks/ids")
    Map<String, List<Long>> getBankIds() {
        var result = new LinkedHashMap<String, List<Long>>();
        result.put("ids", this.service.getBankIds());
        return result;
    }

    @GetMapping("/banks/{id}")
    ResponseDTO.Bank getBank(@PathVariable Long id) {
        return ResponseDTO.Bank.fromModel(this.service.getBank(id));
    }

    ///////////////////////
    // Cards

    @GetMapping("/cards/ids")
    Map<String, List<Long>> getCardIds() {
        var result = new LinkedHashMap<String, List<Long>>();
        result.put("ids", this.service.getCardIds());
        return result;
    }

    @GetMapping("/cards/{id}")
    ResponseDTO.Card getCard(@PathVariable long id) {
        return ResponseDTO.Card.fromModel(this.service.getCard(id));
    }

    /**
     * Add a new card
     * @return The id of the new card:
     * 
     *           {
     *              "id": 123
     *           }
     */
    @PostMapping("/cards")
    Map<String, Long> addCard(@RequestBody RequestDTO.Card card) {
        var result = new LinkedHashMap<String, Long>();
        result.put("id", this.service.addCard(card));
        return result;
    }

    @DeleteMapping("/cards/{id}")
    void deleteCard(@PathVariable long id) {
        this.service.deleteCard(id);
    }

    ///////////////////////
    // Card holders

    @GetMapping("/cardHolders/ids")
    Map<String, List<Long>> getCardHolderIds() {
        var result = new LinkedHashMap<String, List<Long>>();
        result.put("ids", this.service.getCardHolderIds());
        return result;
    }

    ///////////////////////
    // Payments

    @GetMapping("/payments/ids")
    Map<String, List<Long>> getPaymentIds() {
        var result = new LinkedHashMap<String, List<Long>>();
        result.put("ids", this.service.getPaymentIds());
        return result;
    }

    @GetMapping("/payments/{id}")
    ResponseDTO.Payment getPayment(@PathVariable Long id) {
        return ResponseDTO.Payment.fromModel(this.service.getPayment(id));
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
