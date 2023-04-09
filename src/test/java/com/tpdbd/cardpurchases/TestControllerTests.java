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
public class TestControllerTests {
    private final static String BASE_URI = "http://localhost";

    @LocalServerPort
    private int port;

    @BeforeEach
    public void configureRestAssured() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = port;
    }

    @Test
    public void testGetBanksCuits() {
        given()
            .when()
                .get("/test/banks/cuits")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("cuits", Matchers.hasSize(Matchers.greaterThan((0))));
    }

    @Test
    public void testGetBank() {
        var cuit = getSomeBankCuit();

        given()
            .when()
                .get("/test/banks/{cuit}", cuit)
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("cuit", Matchers.equalTo(cuit));
    }

    @Test
    public void testAddCard() {
        var cuit = getSomeBankCuit();
        var dni = getSomeCardHolderDni();

        var card = new RequestDTO.Card(
            cuit, 
            dni, 
            "213123123123", // TODO Use faker
            "123", // TODO Use faker
            LocalDate.now(), 
            LocalDate.now().plusDays(10));

        // Create a new card
        var response = 
            given()
                .when()
                    .contentType(ContentType.JSON)    
                    .body(card)
                    .post("/test/cards");

        response
            .then()
                .statusCode(200);
        
        var number = response.getBody().asString();

        // Validate the creation
        given()
            .when()
                .get("/test/cards/{number}", number)
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("number", Matchers.equalTo(number));
                // TODO Validate other attributes

            
        // Remove the new card
        given()
            .when()
                .delete("/test/cards/{number}", number)
            .then()
                .statusCode(200);
    }


    ///////////////////
    // Helpers

    static public String getSomeBankCuit() {
        return given()
            .get("/test/banks/cuits")
            .jsonPath()
            .getObject("cuits[0]", String.class);
    }

    static public String getSomeCardHolderDni() {
        return given()
            .get("/test/cardHolders/dnis")
            .jsonPath()
            .getObject("dnis[0]", String.class);
    }

}
