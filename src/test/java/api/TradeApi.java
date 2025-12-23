package api;
import utils.ConfigReader;
import endpoints.urls;
import static io.restassured.RestAssured.*;

public class TradeApi {
    
    
        public static String createTrade(String payload) {
            return given()
                    .contentType(ConfigReader.get("content.type"))
                    .body(payload)
                .when()
                    .post(urls.CREATE_TRADE)
                .then()
                    .statusCode(201)
                    .extract()
                    .path("tradeId");
        }
    
        public static void amendTrade(String tradeId, String payload) {
            given()
                .pathParam("id", tradeId)
                .body(payload)
            .when()
                .put(urls.AMEND_TRADE)
            .then()
                .statusCode(200);
        }
    
        public static void confirmTrade(String tradeId) {
            given()
                .pathParam("id", tradeId)
            .when()
                .post(urls.CONFIRM_TRADE)
            .then()
                .statusCode(200);
        }
    
        public static void settleTrade(String tradeId, String payload) {
            given()
                .pathParam("id", tradeId)
                .body(payload)
            .when()
                .post(urls.SETTLE_TRADE)
            .then()
                .statusCode(200);
        }
}
    

