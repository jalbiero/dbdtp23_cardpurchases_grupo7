package com.tpdbd.cardpurchases.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tpdbd.cardpurchases.dto.RequestDTO;
import com.tpdbd.cardpurchases.dto.ResponseDTO;
import com.tpdbd.cardpurchases.errors.BadRequestException;
import com.tpdbd.cardpurchases.services.CardPurchasesService;

@RestController
public class CardPurchasesController {
    @Autowired
    private CardPurchasesService service;

    /***
     * Endpoint to check if  the application is up and running 
     * 
     * URL:
     *      GET /
     * 
     * Return:
     *      The name of the application
     */
    @GetMapping("/")
    String index() {
        return "Card Purchases application";
    }

    /**
     * Adds a new Discount promotion to the speficied bank (identified by its CUIT)
     * 
     * URL: 
     *      POST /banks/{cuit}/addDiscountPromotion
     * 
     * ContentType: 
     *      application/json
     * 
     * Params:
     *  - URL: {cuit} string that indentifies the bank
     *  - Body:
     *      {
     *          "code":               "Promotion code",
     *          "promotionTitle":     "Promotion title", 
     *          "nameStore":          "Store name", 
     *          "cuitStore":          "Store CUIT",
     *          "validityStartDate":  "2020/10/10",
     *          "validityEndDate":    "2023/12/31", 
     *          "comments":           "Some extra comments", 
     *          "discountPercentage": 10.5, 
     *          "priceCap":           10000.0, 
     *          "onlyCash":           true
     *      }
     */
    @PostMapping("/banks/{cuit}/addDiscountPromotion")
    void banksAddDiscountPromotion(@PathVariable String cuit, @RequestBody RequestDTO.Discount discount) {
        this.service.banksAddDiscountPromotion(cuit, discount);
    }

    /**
     * Update the dates of the specified payment indentified by its code
     * 
     * URL: 
     *      PUT /payments/{code}/updateDates
     * 
     * ContentType: 
     *      application/json
     * 
     * Params:
     *  - URL: {code} string that indentifies the payment
     *  - Body:
     *      {
     *          "firstExpiration": "2023/10/31", 
     *          "secondExpiration": "2023/11/15"
     *      }
     */    
    @PutMapping("/payments/{code}/updateDates")
    void paymentsUpdateDates(@PathVariable String code, @RequestBody RequestDTO.PaymentDates paymentDates) {
        if (paymentDates.firstExpiration().isAfter(paymentDates.secondExpiration())) {
            throw new BadRequestException(
                "Second expiration date (%s) must be grater than the fist one (%s)", 
                paymentDates.secondExpiration(), 
                paymentDates.firstExpiration());
        }
        
        this.service.paymentsUpdateDates(code, paymentDates.firstExpiration(), paymentDates.secondExpiration());
    }

    /**
     * Lists cards that expire in the following days (by default 30 days starting
     * from the moment of calling this endpoint if the days and the date are not 
     * specified)
     *
     * URL: 
     *      GET /cards/getSoonToExpire
     * 
     * ContentType: 
     *      application/json
     * 
     * Params:
     *  - URL: 
     *  - Body (optional):
     *      {
     *          "baseDate": "2023/10/15",       
     *          "daysFromBaseDate": 25 
     *      }     
     * 
     * Return:
     *      A list of Cards:
     * 
     *      [
     *          {
     *              "number": "44756745756",
     *              "ccv", "123"
     *              "cardholderNameInCard": "Juan Perez",
     *              "since": "2023/01/01",
     *              "expirationDate": "2026/01/01",
     *              "bankCuit": "23-1231231231-9", 
     *              "userDni": "30123978"          
     *          },
     *          ...
     *      ]
     */
    @GetMapping("/cards/getSoonToExpire")
    List<ResponseDTO.Card> cardsGetSonToExpired(@RequestBody(required=false) 
                                                Optional<RequestDTO.SoonToExpire> soonToExpire) 
    {
        var params = soonToExpire.orElse(new RequestDTO.SoonToExpire());
        var cards = this.service.cardsGetSoonToExpire(params.baseDate(), params.daysFromBaseDate());

        return cards.stream()
            .map(ResponseDTO.Card::fromModel)
            .toList();
    }


    /**
     * Lists purchases from the specfied card in the specified store
     * 
     * TODO complete documentation
     * 
     */
    @GetMapping("/cards/getPurchaseInformation")
    List<ResponseDTO.Purchase> cardsGetPurchaseInformation(@RequestBody RequestDTO.PurchaseLocator purchaseLocator) {
        var purchases = this.service.getPurchases(purchaseLocator.cuitStore(), purchaseLocator.cardNumber());
    
        return purchases.stream()
            .map(ResponseDTO.Purchase::fromModel)
            .toList();
    }
}
