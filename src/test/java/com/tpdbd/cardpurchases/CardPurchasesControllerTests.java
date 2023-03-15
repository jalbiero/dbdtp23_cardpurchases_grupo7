package com.tpdbd.cardpurchases;

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
    public void testAddDiscountPromotion() {
        var discount = new Discount(
            "1000", "Rebaja 20% contado", "Zapas Store", "123123123", LocalDate.now(), LocalDate.now().plusMonths(3), "", 0.20f, 1000f, true);

        given()
            .when()
                .body(discount)
                .contentType(ContentType.JSON) 
                .post("/bank/cuit1/addDiscountPromotion")
            .then()
                .statusCode(200);
    }
}
