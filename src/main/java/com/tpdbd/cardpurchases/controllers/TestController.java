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
    ResponseDTO.Ids getBankIds() {
        return new ResponseDTO.Ids(this.service.getBankIds());
    }

    @GetMapping("/banks/{id}")
    ResponseDTO.Bank getBank(@PathVariable String id) {
        return ResponseDTO.Bank.fromModel(this.service.getBank(id));
    }

    ///////////////////////
    // Cards

    @GetMapping("/cards/ids")
    ResponseDTO.Ids getCardIds() {
        return new ResponseDTO.Ids(this.service.getCardIds());
    }

    @GetMapping("/cards/{id}")
    ResponseDTO.Card getCard(@PathVariable String id) {
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
    ResponseDTO.Id addCard(@RequestBody RequestDTO.Card card) {
        return new ResponseDTO.Id(this.service.addCard(card));
    }

    @DeleteMapping("/cards/{id}")
    void deleteCard(@PathVariable String id) {
        this.service.deleteCard(id);
    }

    ///////////////////////
    // Card holders

    @GetMapping("/cardHolders/ids")
    ResponseDTO.Ids getCardHolderIds() {
        return new ResponseDTO.Ids(this.service.getCardHolderIds());
    }

    ///////////////////////
    // Payments

    @GetMapping("/payments/ids")
    ResponseDTO.Ids getPaymentIds() {
        return new ResponseDTO.Ids(this.service.getPaymentIds());
    }

    @GetMapping("/payments/{id}")
    ResponseDTO.Payment getPayment(@PathVariable String id) {
        return ResponseDTO.Payment.fromModel(this.service.getPayment(id));
    }

    ///////////////////////
    // Purchases

    @GetMapping("/purchases/ids")
    ResponseDTO.Ids getPurchaseIds() {
        return new ResponseDTO.Ids(this.service.getPurchaseIds());
    }

    @GetMapping("/purchases/creditIds")
    ResponseDTO.Ids getPurchaseCreditIds() {
        return new ResponseDTO.Ids(this.service.getPurchaseCreditIds());
    }

    ///////////////////////
    // Stores

    @GetMapping("/stores/cuits")
    ResponseDTO.Cuits getStoreCuits() {
        return new ResponseDTO.Cuits(this.tdgService.getStoreCuits());
    }

    ///////////////////////
    // Promotions

    @GetMapping("/promotions/codes")
    ResponseDTO.PromoCodes getPromotionCodes() {
        return new ResponseDTO.PromoCodes(this.service.getPromotionCodes());
    }
}
