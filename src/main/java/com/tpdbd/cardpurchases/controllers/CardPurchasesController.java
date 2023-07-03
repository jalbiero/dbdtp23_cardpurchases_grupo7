package com.tpdbd.cardpurchases.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tpdbd.cardpurchases.dto.RequestDTO;
import com.tpdbd.cardpurchases.dto.ResponseDTO;
import com.tpdbd.cardpurchases.errors.BadRequestException;
import com.tpdbd.cardpurchases.services.CardPurchasesService;

// Note: Number in the documentation of each endpoint is correlated with
//       the one in the service class (CardPurchaseService)
@RestController
public class CardPurchasesController {
    @Autowired
    private CardPurchasesService service;

    /***
     * Endpoint to check if the application is up and running 
     * 
     * URL:
     *      GET /
     * 
     * Return:
     *      The name of the application
     * 
     */
    @GetMapping("/")
    String index() {
        return "Card Purchases application";
    }

    /** 
     * 01 - Adds a new Discount promotion to the speficied bank (identified by its CUIT)
     * 
     * URL: 
     *      POST /banks/{cuit}/addDiscountPromotion
     * 
     * ContentType: 
     *      application/json
     * 
     * Params:
     *      - URL: {cuit} string that indentifies the bank
     *      - Body:
     *          {
     *              "code":               "Promotion code",
     *              "promotionTitle":     "Promotion title", 
     *              "nameStore":          "Store name", 
     *              "cuitStore":          "Store CUIT",
     *              "validityStartDate":  "2020/10/10",
     *              "validityEndDate":    "2023/12/31", 
     *              "comments":           "Some extra comments", 
     *              "discountPercentage": 10.5, 
     *              "priceCap":           10000.0, 
     *              "onlyCash":           true
     *          }
     */
    @PostMapping("/banks/{cuit}/addDiscountPromotion")
    void banksAddDiscountPromotion(@PathVariable String cuit, @RequestBody RequestDTO.Discount discount) {
        this.service.banksAddDiscountPromotion(cuit, discount);
    }

    /**
     * 02 - Update the dates of the specified payment indentified by its code
     * 
     * URL: 
     *      PUT /payments/{code}/updateDates
     * 
     * ContentType: 
     *      application/json
     * 
     * Params:
     *      - URL: {code} string that indentifies the payment
     *      - Body:
     *          {
     *              "firstExpiration": "2023/10/31", 
     *              "secondExpiration": "2023/11/15"
     *          }
     */    
    @PutMapping("/payments/{code}/updateDates")
    void paymentsUpdateDates(@PathVariable String code, 
                             @RequestBody RequestDTO.PaymentsUpdateDatesBody body) 
    {
        if (body.firstExpiration().isAfter(body.secondExpiration())) {
            throw new BadRequestException(
                "Second expiration date (%s) must be grater than the first one (%s)", 
                body.secondExpiration(), 
                body.firstExpiration());
        }
        
        this.service.paymentsUpdateDates(code, body.firstExpiration(), body.secondExpiration());
    }

    /**
     * 04 - Lists cards that expire in the following days (by default 30 days starting
     * from the moment of calling this endpoint if the days and the date are not 
     * specified)
     *
     * URL: 
     *      GET /cards/soonToExpire
     * 
     * ContentType: 
     *      application/json
     * 
     * Params:
     *      - Body (optional):
     *          {
     *              "baseDate": "2023/10/15",       
     *              "daysFromBaseDate": 25 
     *          }     
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
    @GetMapping("/cards/soonToExpire")
    List<ResponseDTO.Card> cardsGetSoonToExpired(@RequestBody(required=false) 
                                                 Optional<RequestDTO.CardsGetSoonToExpiredBody> body) 
    {
        var params = body.orElse(new RequestDTO.CardsGetSoonToExpiredBody());
        var cards = this.service.cardsGetSoonToExpire(params.baseDate(), params.daysFromBaseDate());

        return cards.stream()
            .map(ResponseDTO.Card::fromModel)
            .toList();
    }

    /**
     * 05 - Gets information about the specified purchase
     * 
     * URL: 
     *      GET /purchases/{id}
     * 
     * ContentType: 
     *      application/json
     * 
     * Params:
     *      - URL: {id} the purchase id
     * 
     * Return:
     *      The purchase information:
     * 
     *      {
     *          "id": 12
     *          "type": "CashPurchase" | "CreditPurchase"
     *          "cardNumber": "1123123123123",
     *          "PaymentVoucher": "voucherCode",
     *          "store": "the name of the store",
     *          "cuitStore": "23-123123123-1",
     *          "ammount": 5012.50,
     *          "finalAmount": 6000.0,
     *          "storeDiscount": 10.0         << Only available when type = "CashPurchase"
     *          "interest": 15.0,             << Only available when type = "CreditPurchase"
     *          "numberOfQuotas": 5,          << Only available when type = "CreditPurchase"
     *          "quotas: [                    << Only one quota when type = "CashPurchase"
     *              {
     *                  "number": 1, 
     *                  "price": 450.50, 
     *                  "month": 10, 
     *                  "year": 2023, 
     *                  "store": "The name of the store",  
     *                  "cardNumber": "1123123123123"
     *              },
     *              ...
     *          ]
     *      }
     */
    @GetMapping("/purchases/{id}")
    ResponseDTO.Purchase purchasesGetInfo(@PathVariable Long id) {
        return ResponseDTO.Purchase.fromModel(this.service.purchasesGetInfo(id));
    }

    /**
     * 06 - Delete a specified promotion
     * 
     * URL: 
     *      DELETE /promotions/{code}
     *
     * Params:
     *      - URL: {code} Promotion code
     * 
     * Return:
     *      404 if promotion code could not be found  
     * 
     */
    @DeleteMapping("/promotions/{code}")
    void promotionsDelete(@PathVariable String code) {
        this.service.promotionsDelete(code);
    }


    /**
     * 08 - List promotions for the specified store in the specified date range
     * 
     * URL: 
     *      GET /stores/{cuit}/availablePromotions
     * 
     * ContentType: 
     *      application/json
     * 
     * Params:
     *      - URL: {cuit} the CUIT store
     *      - Body:
     *          {
     *              "from": "2021-12-31",
     *              "to": "2022-09-20"
     *          }     
     * 
     * Return:
     *      A list of store promotions:
     * 
     *      [
     *          {
     *              "code": "promo114",
     *              "promotionTitle": "definición transicional Pre-emptivo",
     *              "nameStore": "Pardo S.R.L.",
     *              "cuitStore": "0",
     *              "validityStartDate": "2018-10-27",
     *              "validityEndDate": "2019-06-12",
     *              "comments": "Douglas Reynholm",
     *              "discountPercentage": 0.15,
     *              "priceCap": 6546.0,
     *              "type": "Discount"
     *          },
     *          ...
     *      ]
     *
     */
    @GetMapping("/stores/{cuit}/availablePromotions")
    List<ResponseDTO.Promotion> storesGetAvailablePromotions(
        @PathVariable String cuit, 
        @RequestBody RequestDTO.StoresGetAvailablePromotionsBody body) 
    {
        var promotions = this.service.storesGetAvailblePromotions(cuit, body.from(), body.to());
    
        return promotions.stream()
            .map(ResponseDTO.Promotion::fromModel)
            .toList();
    }

    /**
     * 11 - Returns the best seller store for the speficied year and month
     * 
     * URL: 
     *      GET /stores/bestSeller?year={someYear}&month={someMonth}
     * 
     * URL params:
     *      - URL: {someYear} the specified year
     *             {someMonth} the speficified month
     * 
     * Return:
     *      The store found (name, cuit and profit for the specified year and month)
     *      or 404 otherwise
     * 
     *      {
     *          "name":"Roman Caro e Hijos",
     *          "cuit":"30",
     *          "profit":59118.285
     *      }
     *
     */
    @GetMapping("/stores/bestSeller")
    ResponseDTO.Store storesGetBestSeller(@RequestParam int year, @RequestParam int month) {
        return this.service.storesGetBestSeller(year, month);
    }
}
