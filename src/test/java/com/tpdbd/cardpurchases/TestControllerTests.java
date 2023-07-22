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
import net.datafaker.Faker;

import static io.restassured.RestAssured.given;

import java.time.LocalDate;


/**
 * Integration tests for helper endpoints
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestControllerTests {
    private final static String BASE_URI = "http://localhost";
    private Faker faker = new Faker();

    @LocalServerPort
    private int port;

    @BeforeEach
    public void configureRestAssured() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = port;
    }

    @Test
    public void testGetBanksIds() {
        given()
            .when()
                .get("/test/banks/ids")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("ids", Matchers.hasSize(Matchers.greaterThan((0))));
    }

    @Test
    public void testGetBank() {
        var id = getSomeBankId();

        given()
            .when()
                .get("/test/banks/{id}", id)
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", Matchers.equalTo((int)id));
    }

    @Test
    public void testAddRemoveCard() {
        final var CARD_NUMBER = this.faker.business().creditCardNumber();

        var bankId = getSomeBankId();
        var dni = getSomeCardHolderDni();

        var card = new RequestDTO.Card(
            bankId, 
            dni, 
            CARD_NUMBER, 
            this.faker.business().securityCode(),
            LocalDate.now(), 
            LocalDate.now().plusDays(10));

        // Create a new card
        given()
            .when()
                .contentType(ContentType.JSON)    
                .body(card)
                .post("/test/cards")
            .then()
                .statusCode(200);
        
        // Validate the creation
        given()
            .when()
                .get("/test/cards/{number}", CARD_NUMBER)
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("number", Matchers.equalTo(CARD_NUMBER));
                // TODO Validate other attributes

            
        // Remove the new card
        given()
            .when()
                .delete("/test/cards/{number}", CARD_NUMBER)
            .then()
                .statusCode(200);

        given()
            .when()
                .get("/cards/{number}", CARD_NUMBER)
            .then()
                .statusCode(404);                
    }

    @Test
    public void testGetPromotionCodes() {
        given()
            .when()
                .get("/test/promotions/codes")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("codes", Matchers.hasSize(Matchers.greaterThan((0))));
    }
    

    ///////////////////
    // Helpers

    static public long getSomeBankId() {
        return given()
            .get("/test/banks/ids")
            .jsonPath()
            .getObject("ids[0]", Long.class);
    }

    static public String getSomeCardHolderDni() {
        return given()
            .get("/test/cardHolders/dnis")
            .jsonPath()
            .getObject("dnis[0]", String.class);
    }
}
