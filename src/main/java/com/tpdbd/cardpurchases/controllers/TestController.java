package com.tpdbd.cardpurchases.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpdbd.cardpurchases.dto.ResponseDTO;
import com.tpdbd.cardpurchases.services.TestService;

/**
 * This controller has all endpoints necessary for manual or automated testing
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private TestService service;

    @GetMapping("/")
    String index() {
        return "Card Purchases application - Test API";
    }

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
}
