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
import io.restassured.response.ValidatableResponse;
import net.datafaker.Faker;

import static io.restassured.RestAssured.given;

import java.time.LocalDate;
import java.util.function.Function;

// @formatter:off

/**
 * Integration tests for Card Purchase Application
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CardPurchasesControllerTests {
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

        var cuit = getSomeBankCuit();

        // Add a new promotion
        var discount = new RequestDTO.Discount(
            CODE, TITLE, NAME, CUIT, LocalDate.now(), LocalDate.now().plusMonths(3), 
            COMMENT, DISCOUNT, PCAP, true);

        given()
            .when()
                .contentType(ContentType.JSON)    
                .body(discount)
                .post("/banks/{cuit}/addDiscountPromotion", cuit)
            .then()
                .statusCode(200);

        // Check if the promotion was added to the given bank
        given()
            .when()
                .get("/test/banks/{cuit}", cuit)
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
        final var NEW_DATES = new RequestDTO.PaymentsUpdateDatesBody(
            LocalDate.of(2030, 12, 31), LocalDate.of(2040, 10, 15));

        var code = getSomePaymentCode();

        // Update dates
        given()
            .when()
                .contentType(ContentType.JSON)    
                .body(NEW_DATES)
                .put("/payments/{code}/updateDates", code)
            .then()
                .statusCode(200);

        // Check payment
        given()
            .when()
                .get("/test/payments/{code}", code)
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
    public void testCardsGetSoonToExpire() {
        // Some unique date (the database already has data from TestDataGeneratorService)
        // in order to get just the card that it is being created/tested
        final var BASE_DATE = LocalDate.of(3000, 11, 1); 
        final var DAYS_TO_EXPIRATION = 31;
        final var CARD_NUMBER = this.faker.business().creditCardNumber();

        // Create a new card
        var card = new RequestDTO.Card(
            getSomeBankCuit(), 
            getSomeCardHolderDni(), 
            CARD_NUMBER,
            this.faker.business().securityCode(),
            BASE_DATE,
            BASE_DATE.plusDays(DAYS_TO_EXPIRATION)); 

        given()
            .contentType(ContentType.JSON)                    
            .body(card)
            .post("/test/cards");
        
        // Test some expiration paths

        Function<RequestDTO.CardsGetSoonToExpiredBody, ValidatableResponse> getSoonToExpire = 
            (body) -> {
                return given()
                    .when()
                        .contentType(ContentType.JSON)    
                        .body(body)
                        .get("/cards/soonToExpire")
                    .then()
                        .statusCode(200)
                        .contentType(ContentType.JSON);
            };


        var noCardsInTheNext30days = new RequestDTO.CardsGetSoonToExpiredBody(BASE_DATE, 30);
        getSoonToExpire.apply(noCardsInTheNext30days)
            .body("$", Matchers.hasSize(0));
            
        var oneCardInTheNext30days = new RequestDTO.CardsGetSoonToExpiredBody(BASE_DATE.plusDays(5), 30);
        getSoonToExpire.apply(oneCardInTheNext30days)
            .body("$", Matchers.hasSize(1))
            .body("[0].number", Matchers.equalTo(CARD_NUMBER));

        var noCardsAllAlreadyExpired = new RequestDTO.CardsGetSoonToExpiredBody(BASE_DATE.plusDays(DAYS_TO_EXPIRATION + 1), 30);
        getSoonToExpire.apply(noCardsAllAlreadyExpired)
            .body("$", Matchers.hasSize(0));

        // Remove the test card
        given().delete("/test/cards/{number}", CARD_NUMBER);
    }

    @Test
    public void testCardsGetPurchases() {
        var cardNumber = getSomeCardNumber();

        // Get all card purchases
        var response = 
            given()
                .when()
                    .get("/cards/{number}/purchases", cardNumber);
        response
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", Matchers.hasSize(Matchers.greaterThan(0)))
                .body("[0].cardNumber", Matchers.equalTo(cardNumber))
                .body("[0].quotas", Matchers.hasSize(Matchers.greaterThan(0)));

        // Get purchases in some store                
        var cuitStore = response.jsonPath().getString("[0].cuitStore");
        var totalNumOfPurchases = response.jsonPath().getList("$").size();

        var body = new RequestDTO.CardsGetPurchasesBody(cuitStore);

        given()
            .when()
                .contentType(ContentType.JSON)
                .body(body)
                .get("/cards/{number}/purchases", cardNumber)
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", Matchers.hasSize(Matchers.lessThanOrEqualTo(totalNumOfPurchases)))
                .body("[0].cuitStore", Matchers.equalTo(cuitStore));
    }

    ///////////////////
    // Helpers

    public String getSomeBankCuit() {
        return given()
            .get("/test/banks/cuits")
            .jsonPath()
            .getObject("cuits[0]", String.class);
    }

    public String getSomeCardHolderDni() {
        return given()
            .get("/test/cardHolders/dnis")
            .jsonPath()
            .getObject("dnis[0]", String.class);
    }

    public String getSomePaymentCode() {
        return given()
            .get("/test/payments/codes")
            .jsonPath()
            .getObject("codes[0]", String.class);
    }

    public String getSomeCardNumber() {
        return given()
            .get("/test/cards/numbers")
            .jsonPath()
            .getObject("numbers[0]", String.class);
    }
}
