package com.tpdbd.cardpurchases.controllers;

import java.time.LocalDate;
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
     * 03 - Get card monthly payment with its purchases
     * 
     * URL: 
     *      PUT /cards/{number}/monthlyPayment?year={someYear}&month={someMonth}
     * 
     * URL params:
     *      - URL: {someYear} the specified year
     *             {someMonth} the specified month
     * 
     * Return:
     *      {
     *          "cardNumber", "5876-1948-6884-1575",
     *          "year": 2021,
     *          "month": 8,
     *          "totalPrice": 72423.0,
     *          "purchases": [
     *             {
     *                 "id": 2,
     *                 "cardNumber": "5876-1948-6884-1575",
     *                 "PaymentVoucher": null,
     *                 "store": "Florentin S.A.",
     *                 "cuitStore": "14",
     *                 "ammount": 36740.8,
     *                 "finalAmount": 36740.8,
     *                 "quotas": [
     *                     {
     *                         "number": 1,
     *                         "price": 36740.8,
     *                         "month": 8,
     *                         "year": 2021,
     *                         "store": "Florentin S.A.",
     *                         "cardNumber": "5876-1948-6884-1575"
     *                     }
     *                 ],
     *                 "storeDiscount": 0.0,
     *                 "type": "CashPurchase"
     *              },
     *              ...
     *          ]
     *      }
     */
    @GetMapping("/cards/{number}/monthlyPayment")
    ResponseDTO.MonthlyPayment cardsGetMonthtlyPayment(@PathVariable String number, 
                                                       @RequestParam int year, 
                                                       @RequestParam int month)
    {
        return this.service.cardsGetMonthtlyPayment(number, year, month);
    }

    /**
     * 04 - Lists cards that expire in the following days (by default 30 days starting
     *      from the moment of calling this endpoint if the days and the date are not 
     *      specified)
     * 
     *      Note: GET endpoints shouldn't use a request body (although most web framework supported
     *            it), but current RFC is not against them, and some companies also use them. See 
     *            the following for more information: 
     *                 https://stackoverflow.com/questions/978061/http-get-with-request-body 
     *            Having said that, this particular endpoint use an optional request body.
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
     *              "daysFromBaseDate": 30 
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
        return this.service.cardsGetSoonToExpire(params.baseDate(), params.daysFromBaseDate());
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
        return this.service.purchasesGetInfo(id);
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
     *      GET /stores/{cuit}/availablePromotions?from={from}&to={to}
     * 
     * Params:
     *      - URL: {from} the specified 'from' date
     *             {to} the specified 'to' date
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
    List<ResponseDTO.Promotion> storesGetAvailablePromotions(@PathVariable String cuit, 
                                                             @RequestParam LocalDate from,
                                                             @RequestParam LocalDate to)
    {
        return this.service.storesGetAvailblePromotions(cuit, from, to);
    }

    /**
     * 09 - Gets the top 10 cards (and their respective owners) with more purchases
     * 
     * URL: 
     *      GET /cards/getTop10Purchasers
     * 
     * Return:
     *      The top 10 list sorted by numOfPurchases in descending order:
     * 
     *      [
     *          {
     *              "cardHolderName": "Nanci Gonzalez Zamora",
     *              "numOfPurchases": 20,
     *              "cardNumber": "2225-7984-4074-9687"
     *          },
     *          {
     *              "cardHolderName": "Eliseo Castillo Peña",
     *              "numOfPurchases": 19,
     *              "cardNumber": "1800-3054-0373-2545"
     *          },
     *          ...
     *      ]
     */
    @GetMapping("/cards/getTop10Purchasers")
    List<ResponseDTO.PurchaserCardHolder> cardsGetTop10Purchasers() {
        return this.service.cardsGetTop10Purchasers();
    }

    /**
     * 10 - Gets the most used promotion across all purchases. 
     * 
     * URL: 
     *      GET /promotions/theMostUsed
     * 
     * Return:
     *      {
     *          "code": "promo129",
     *          "promotionTitle": "conjunto de instrucciones generada por el cliente Fácil",
     *          "nameStore": "Albornoz, Argañaraz y Bustamante Asociados",
     *          "cuitStore": "12",
     *          "validityStartDate": "2016-04-05",
     *          "validityEndDate": "2018-02-04",
     *          "comments": "David Garfield",
     *          "numberOfQuotas": 8,
     *          "interest": 0.06,
     *          "numOfPurchases": 6,
     *          "type": "Financing"
     *      }
     */
    @GetMapping("/promotions/theMostUsed")
    ResponseDTO.Promotion promotionsGetTheMostUsed() {
        return this.service.promotionsGetTheMostUsed();
    }

    /**
     * 11 - Returns the best seller store for the speficied year and month
     * 
     * URL: 
     *      GET /stores/bestSeller?year={someYear}&month={someMonth}
     * 
     * URL params:
     *      - URL: {someYear} the specified year
     *             {someMonth} the specified month
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

    /**
     * 12 - Gets the bank with most payment values in its cards
     * 
     * URL: 
     *      GET /banks/theOneWithMostPaymentValues
     * 
     * Return:
     *      {
     *          "name": "Gonzalez Ocampo S.R.L.",
     *          "cuit": "52",
     *          "address": "Avda. Cintia Chaves 16254 - Puerta blanca, Lanus, ER (2528)",
     *          "telephone": "0371 462-3380",
     *          "totalPaymentValueFromItsCards": 500000.76
     *          "promotions": [
     *              {
     *                  "code": "promo45",
     *                  "promotionTitle": "jerarquía explícita Centrado en el negocio",
     *                  "nameStore": "Avalos, Yapura y Cordoba Asociados",
     *                  "cuitStore": "46",
     *                  "validityStartDate": "2022-08-23",
     *                  "validityEndDate": "2022-09-22",
     *                  "comments": "Tony Longworth",
     *                  "numberOfQuotas": 5,
     *                  "interest": 0.09,
     *                  "type": "Financing"
     *              },
     *              ...
     *          ]
     *      }
     */
    @GetMapping("/banks/theOneWithMostPaymentValues")
    ResponseDTO.Bank banksTheOneWithMostPaymentValues() {
        return this.service.banksGetTheOneWithMostPaymentValues();
    }
}
