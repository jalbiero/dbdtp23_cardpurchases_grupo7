package com.tpdbd.cardpurchases;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

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

}
