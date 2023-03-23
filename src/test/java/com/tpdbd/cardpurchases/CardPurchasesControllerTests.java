package com.tpdbd.cardpurchases;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.tpdbd.cardpurchases.model.Discount;

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
    public void testGetBanksCuits() {
        given()
            .when()
                .get("/tests/banks/cuits")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("cuits", Matchers.hasSize(Matchers.greaterThan((0))));
    }

    @Test
    public void testBanksGetBank() {
        var cuit = given()
                .get("/tests/banks/cuits")
                .jsonPath()
                .getObject("cuits[0]", String.class);

        given()
            .when()
                .get(String.format("/tests/banks/%s", cuit))
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("cuit", Matchers.equalTo(cuit));
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
                .get("/tests/banks/cuits")
                .jsonPath()
                .getObject("cuits[0]", String.class);

        // Add a new promotion
        var discount = new Discount(
            CODE, TITLE, NAME, CUIT, LocalDate.now(), LocalDate.now().plusMonths(3), 
            COMMENT, DISCOUNT, PCAP, true);

        given()
            .when()
                .body(discount)
                .contentType(ContentType.JSON)
                .post(String.format("/banks/%s/addDiscountPromotion", cuit))
            .then()
                .statusCode(200);

        // Check if the promotion was added for the given bank
        given()
            .when()
                .get(String.format("/tests/banks/%s", cuit))
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
}

