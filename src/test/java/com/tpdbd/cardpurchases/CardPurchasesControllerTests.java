package com.tpdbd.cardpurchases;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.tpdbd.cardpurchases.dto.RequestDTO;
import com.tpdbd.cardpurchases.dto.ResponseDTO;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import net.datafaker.Faker;

import static io.restassured.RestAssured.given;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;


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

        var cuit = getSomeBankId();

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
    public void testPaymentsUpdateDates() {
        final var NEW_DATES = new RequestDTO.PaymentsUpdateDatesBody(
            LocalDate.of(2030, 12, 31), LocalDate.of(2040, 10, 15));

        var id = getSomePaymentId();

        // Update dates
        given()
            .when()
                .contentType(ContentType.JSON)    
                .body(NEW_DATES)
                .put("/payments/{id}/updateDates", id)
            .then()
                .statusCode(200);

        // Check payment
        given()
            .when()
                .get("/test/payments/{id}", id)
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
    void testCardsGetMonthtlyPaymentHappyPath() {
        // TODO This test is not well designed because the card id, year and month
        //      are hardcoded (test data is repeatable, but if not, the test will fail.
        //      See TestDataGeneratorService.random for more information about repeatable data)

        final var CARD_ID = 76l; 
        final var YEAR = 2019;
        final var MONTH = 3;

        given()
            .when()
                .get("/cards/{id}/monthlyPayment?year={year}&month={month}", CARD_ID, YEAR, MONTH) 
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("cardId", Matchers.equalTo((int)CARD_ID))
                .body("year", Matchers.equalTo(YEAR))
                .body("month", Matchers.equalTo(MONTH))
                .body("purchases", Matchers.not(Matchers.emptyArray()));
    }

    @Test 
    void testCardsGetMonthtlyPaymentNotFound() {
        final var CARD_ID = 999_999_999_999l;
        final var UNREAL_YEAR = 1900;
        final var MONTH = 1;

        given()
            .when()
                .get("/cards/{id}/monthlyPayment?year={year}&month={month}", CARD_ID, UNREAL_YEAR, MONTH) 
            .then()
                .statusCode(404);
    }

    @Test
    public void testCardsGetSoonToExpire() {
        // Some unique date (the database already has data from TestDataGeneratorService)
        // in order to get just the card that it is being created/tested
        final var BASE_DATE = LocalDate.of(3000, 11, 1); 
        final var DAYS_TO_EXPIRATION = 31;
        final var CARD_NUMBER = this.faker.business().creditCardNumber();

        // Create a new card
        var newCard = new RequestDTO.Card(
            getSomeBankId(), 
            getSomeCardHolderIds(), 
            CARD_NUMBER,
            this.faker.business().securityCode(),
            BASE_DATE,
            BASE_DATE.plusDays(DAYS_TO_EXPIRATION)); 

        var newCardId = given()
            .contentType(ContentType.JSON)                    
            .body(newCard)
            .post("/test/cards")
            .jsonPath()
            .getObject("id", Long.class);
        
        // Test some expiration paths

        BiFunction<LocalDate, Integer, ValidatableResponse> getSoonToExpire = 
            (baseDate, daysFromBaseDate) -> {
                return given()
                    .when()
                        .get("/cards/soonToExpire?baseDate={1}&daysFromBaseDate={2}", 
                            baseDate.toString(), daysFromBaseDate)
                    .then()
                        .statusCode(200)
                        .contentType(ContentType.JSON);
            };    

        // No card in the next 30 days starting from BASE_DATE
        getSoonToExpire.apply(BASE_DATE, 30)
            .body("$", Matchers.hasSize(0));
            
        // One card in the next 30 days
        getSoonToExpire.apply(BASE_DATE.plusDays(5), 30)
            .body("$", Matchers.hasSize(1))
            .body("[0].number", Matchers.equalTo(CARD_NUMBER));

        // No cards, all already expired
        getSoonToExpire.apply(BASE_DATE.plusDays(DAYS_TO_EXPIRATION + 1), 30)
             .body("$", Matchers.hasSize(0));

        // Remove the test card
        given()
            .when()
                .delete("/test/cards/{id}", newCardId)
            .then()
                .statusCode(200);
    }

    @Test 
    public void testPurchasesGetInfo() {
        var id = getSomePurchaseId();
     
        given()
            .when()
                .get("/purchases/{id}", id)
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", Matchers.equalTo((int)id))
                .body("quotas", Matchers.hasSize(Matchers.greaterThanOrEqualTo(0)));
    }

    @Test 
    public void testPromotionsDelete() {
        var promoCodes = getPromotionCodes();
        var codeToBeDeleted = promoCodes.get(0);

        given()
            .when()
                .delete("/promotions/{code}", codeToBeDeleted)
            .then()
                .statusCode(200);

        promoCodes = getPromotionCodes();
        assertThat("Promotion not deleted", !promoCodes.contains(codeToBeDeleted));
    }

    @Test
    public void testPurchasesCreditGetTotalPriceHappyPath() {
        // TODO This test is not well designed because the ID is hardcoded. 
        //      (test data is repeatable, but if not, the test will fail.
        //      See TestDataGeneratorService.random for more information about 
        //      repeatable data)

        final var CREDIT_PURCHASE_ID = 901;
     
        given()
            .when()
                .get("/purchases/{id}/creditTotalPrice", CREDIT_PURCHASE_ID)
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", Matchers.equalTo((int)CREDIT_PURCHASE_ID))
                .body("totalPrice", Matchers.greaterThanOrEqualTo(0.f));
    }

    @Test
    public void testPurchasesCreditGetTotalPriceNotFound() {
        // TODO This test is not well designed because the ID is hardcoded. 
        //      (test data is repeatable, but if not, the test will fail.
        //      See TestDataGeneratorService.random for more information about 
        //      repeatable data)

        final var CASH_PURCHASE_ID = 1;
     
        given()
            .when()
                .get("/purchases/{id}/creditTotalPrice", CASH_PURCHASE_ID)
            .then()
                .statusCode(404);
    }

    @Test
    public void testStoresGetAvailblePromotions() {
        BiFunction<String, String, String> makeParams = 
            (from, to) -> String.format("from=%s&to=%s", from, to); 

        var storeCuit = getSomeStoreCuit();

        var response = 
            given()
                .when()
                    .get("/stores/{cuit}/availablePromotions?{params}", 
                        storeCuit, makeParams.apply("1900-01-01", "3000-12-31"));
        response
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", Matchers.hasSize(Matchers.greaterThan(0)));

        // Pick the 1st promotion in order to filter by its dates
        var from = response.jsonPath().getString("[0].validityStartDate");
        var to = response.jsonPath().getString("[0].validityEndDate");

        response = 
            given()
                .when()
                    .get("/stores/{cuit}/availablePromotions?{params}", 
                        storeCuit, makeParams.apply(from, to));
        response
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", Matchers.hasSize(Matchers.equalTo(1)));
    }

    @Test
    public void testCardsGetTop10Purchasers() {
        var response = given()
            .when()
                .get("/cards/getTop10Purchasers");

        response
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", Matchers.hasSize(Matchers.greaterThanOrEqualTo(0)))
                .body("$", Matchers.hasSize(Matchers.lessThanOrEqualTo(10)));
                
        // Validate the data, incluiding the array order (sorted by numOfPurchases in descending order)
        var purchasers = response.jsonPath().getList("$", ResponseDTO.PurchaserCardHolder.class);

        int lastNumOfPurchases = Integer.MAX_VALUE;
        for (var item: purchasers) {
            assertThat(item.cardHolderName(), Matchers.not(Matchers.emptyString()));
            assertThat(item.numOfPurchases(), Matchers.lessThanOrEqualTo(lastNumOfPurchases));
            assertThat(item.cardNumber(), Matchers.not(Matchers.emptyString())); 
            lastNumOfPurchases = item.numOfPurchases();
        }
    }

    @Test
    void testPromotionsGetTheMostUsed() {
        given()
            .when()
                .get("/promotions/theMostUsed")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("code", Matchers.not(Matchers.emptyString()))
                .body("numOfPurchases", Matchers.greaterThan(0));
    }

    @Test
    public void testStoresGetBestSellerHappyPath() {
        // TODO This test is not well designed because the year and month are 
        //       hardcoded (test data is repeatable, but if not, the test will fail.
        //       See TestDataGeneratorService.random for more information about repeatable data)

        given()
            .when()
                .get("/stores/bestSeller?year={year}&month={month}", 2023, 3)
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("name", Matchers.not(Matchers.emptyString()))
                .body("cuit", Matchers.not(Matchers.emptyString()))
                .body("profit", Matchers.greaterThan(0.f));
    }

    @Test
    public void testStoresGetBestSellerNotFound() {
        // No sells in January, 1610 ;-)
        given()
            .when()
                .get("/stores/bestSeller?year={year}&month={month}", 1610, 1)
            .then()
                .statusCode(404);
    }
    
    @Test
    public void testBanksGetTheOneWithMostPaymentValues() {
        given()
            .when()
                .get("/banks/theOneWithMostPaymentValues")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("name", Matchers.not(Matchers.emptyString()))
                .body("cuit", Matchers.not(Matchers.emptyString()))
                .body("totalPaymentValueFromItsCards", Matchers.greaterThan(0.f));
    }


    ///////////////////
    // Helpers

    public String getSomeBankId() {
        return given()
            .get("/test/banks/ids")
            .jsonPath()
            .getObject("ids[0]", String.class);
    }

    public long getSomeCardHolderIds() {
        return given()
            .get("/test/cardHolders/ids")
            .jsonPath()
            .getObject("ids[0]", Long.class);
    }

    public long getSomePaymentId() {
        return given()
            .get("/test/payments/ids")
            .jsonPath()
            .getObject("ids[0]", Long.class);
    }

    static public String getSomeStoreCuit() {
        return given()
            .get("/test/stores/cuits")
            .jsonPath()
            .getObject("cuits[1]", String.class);
    }

    static public long getSomePurchaseId() {
        return given()
            .get("/test/purchases/ids")
            .jsonPath()
            .getObject("ids[0]", Long.class);
    }

    static public List<String> getPromotionCodes() {
        return given()
            .get("/test/promotions/codes")
            .jsonPath()
            .getObject("codes", new TypeRef<ArrayList<String>>(){});
    }

}
