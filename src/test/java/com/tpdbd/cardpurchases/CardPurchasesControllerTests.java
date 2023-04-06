package com.tpdbd.cardpurchases;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.tpdbd.cardpurchases.dto.RequestDTO;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

import java.time.LocalDate;

// @formatter:off

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CardPurchasesControllerTests {
    private final static String BASE_URI = "http://localhost";

    @LocalServerPort
    private int port;

    @BeforeEach
    public void configureRestAssured() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = port;
    }

    @Test
    public void testRoot() {
        given()
            .when()
                .get("/")
            .then()
                .statusCode(200);
    }

    @Test
    public void testBanksAddDiscountPromotion() {
        final var CODE = "1000000";
        final var TITLE = "foo title";
        final var NAME = "foo name";
        final var CUIT = "123123123";
        final var COMMENT = "foo comment";
        final var DISCOUNT = 0.5f;
        final var PCAP = 5000.f;

        // Select some bank
        var cuit = given()
                .get("/test/banks/cuits")
                .jsonPath()
                .getObject("cuits[0]", String.class);

        // Add a new promotion
        var discount = new RequestDTO.Discount(
            CODE, TITLE, NAME, CUIT, LocalDate.now(), LocalDate.now().plusMonths(3), 
            COMMENT, DISCOUNT, PCAP, true);

        given()
            .when()
                .contentType(ContentType.JSON)    
                .body(discount)
                .post(String.format("/banks/%s/addDiscountPromotion", cuit))
            .then()
                .statusCode(200);

        // Check if the promotion was added to the given bank
        given()
            .when()
                .get(String.format("/test/banks/%s", cuit))
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("promotions.code", Matchers.hasItem(CODE))
                .body("promotions.promotionTitle", Matchers.hasItem(TITLE))
                .body("promotions.nameStore", Matchers.hasItem(NAME))
                .body("promotions.cuitStore", Matchers.hasItem(CUIT))
                .body("promotions.comments", Matchers.hasItem(COMMENT))
                .body("promotions.discountPercentage", Matchers.hasItem(DISCOUNT))
                .body("promotions.priceCap", Matchers.hasItem(PCAP));
    }

    @Test
    public void testPaymentsUpdateDate() {
        final var NEW_DATES = new RequestDTO.PaymentDates(
            LocalDate.of(2030, 12, 31), LocalDate.of(2040, 10, 15));

        // Select some payment code
         var code = given()
            .get("/test/payments/codes")
            .jsonPath()
            .getObject("codes[0]", String.class);

        // Update dates
        given()
            .when()
                .contentType(ContentType.JSON)    
                .body(NEW_DATES)
                .put(String.format("/payments/%s/updateDates", code))
            .then()
                .statusCode(200);

        // Check payment
        given()
            .when()
                .get(String.format("/test/payments/%s", code))
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(
                    "firstExpiration", 
                    Matchers.equalTo(NEW_DATES.firstExpiration().toString()))
                .body(
                    "secondExpiration", 
                    Matchers.equalTo(NEW_DATES.secondExpiration().toString()));
    }

    @Test
    public void testCardsGetNextExpired() {
        //var body  = new RequestDTO.NextExpiredCards(LocalDate.of(2000, 12, 31), 10000);
        var body  = new RequestDTO.NextExpiredCards(null, null);

        given()
            .when()
                .contentType(ContentType.JSON)    
                .body(body)
                .get("/cards/getNextExpire")
            .then()
                .statusCode(200);
    }
}
