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
        var cuit = given()
                .get("/test/banks/cuits")
                .jsonPath()
                .getObject("cuits[0]", String.class);

        given()
            .when()
                .get(String.format("/test/banks/%s", cuit))
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("cuit", Matchers.equalTo(cuit));
    }

    @Test
    public void testAddCard() {
         // Select some bank
         var cuit = given()
            .get("/test/banks/cuits")
            .jsonPath()
            .getObject("cuits[0]", String.class);

        // TODO Implement the endpoint to list DNIs          
        var dni = "726-37-4575";

        var card = new RequestDTO.Card(
            cuit, 
            dni, 
            "213123123123", // TODO Use faker
            "123", // TODO Use faker
            LocalDate.now(), 
            LocalDate.now().plusDays(10));

        given()
            .when()
                .contentType(ContentType.JSON)    
                .body(card)
                .post("/test/cards")
            .then()
                .statusCode(200);
                // .contentType(ContentType.JSON)
                // .body("cuit", Matchers.equalTo(cuit));
    }
}
