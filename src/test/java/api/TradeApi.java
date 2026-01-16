package api;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import endpoints.pdfConstants;
import endpoints.urls;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.forwardTradeModel;

public class TradeApi {
    
    
        public static Response createforwardTrade(forwardTradeModel forwardTradePayload) {
            return ApiSpec.baseSpec()
                    .body(forwardTradePayload)
                .when()
                    .post(urls.CREATE_FORWARDTRADE)
                .then()
                    .statusCode(201)
                    .extract().response();
        }
    
        public static void amendTrade(String tradeId, String payload) {
            ApiSpec.baseSpec()
                .pathParam("id", tradeId)
                .body(payload)
            .when()
                .put(urls.AMEND_TRADE)
            .then()
                .statusCode(200);
        }
    
        public static void confirmTrade(String tradeId) {
           ApiSpec.baseSpec()
                .pathParam("id", tradeId)
            .when()
                .post(urls.CONFIRM_TRADE)
            .then()
                .statusCode(200);
        }
    
        public static void settleTrade(String tradeId, String payload) {
           ApiSpec.baseSpec()
                .pathParam("id", tradeId)
                .body(payload)
            .when()
                .post(urls.SETTLE_TRADE)
            .then()
                .statusCode(200);
        }

    public static Response createSpotTrade(Object payload) {
        return ApiSpec.baseSpec()
                .body(payload)
            .when()
                .post(urls.CREATE_SPOTTRADE)
            .then()
                .extract().response();
    }

    public static Response getTradeResponse(String tradeId) {
    return ApiSpec.baseSpec()
            .contentType(ContentType.JSON)
            .pathParam("id", tradeId)
        .when()
            .get(urls.GET_TRADE)
        .then()
            .extract()
            .response();
}

 public static Response confirmationGeneationResponse(int tradeId) {
            return ApiSpec.baseSpec()
            .queryParam("tradeId", tradeId)
                .when()
                    .post(urls.CREATE_CONFIRMATIONDOCUMENT)
                .then()
                    .statusCode(201)
                    .extract().response();
        }

        public static String downloadPdf(int tradeId, String pdfFileName) {

        Response response = ApiSpec.baseSpecForPdf()
                .pathParam("id", tradeId)
                .when()
                    .get(urls.GET_PDFDOCUMENT)
                .then()
                    .statusCode(200)
                    .extract().response();

        byte[] pdfBytes = response.asByteArray();

        Path filePath = Paths.get(
               pdfConstants.ACTUAL_PDF_BASE_PATH,
                pdfFileName
        );

       try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(pdfBytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save PDF file: " + pdfFileName, e);
        }

        return filePath.toString();
    }

}

    

