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
import com.tpdbd.cardpurchases.model.Payment;
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
    Map<String, List<String>> getBanksCuits() {
        var result = new LinkedHashMap<String, List<String>>();
        result.put("cuits", this.service.getBankCuits());
        return result;
    }

    @GetMapping("/banks/{cuit}")
    Bank getBank(@PathVariable String cuit) {
        return this.service.getBank(cuit);
    }

    @GetMapping("/payments/codes")
    Map<String, List<String>> getPaymentCodes() {
        var result = new LinkedHashMap<String, List<String>>();
        result.put("codes", this.service.getPaymentCodes());
        return result;
    }

    @GetMapping("/payments/{code}")
    Payment getPayment(@PathVariable String code) {
        return this.service.getPayment(code);
    }

}
