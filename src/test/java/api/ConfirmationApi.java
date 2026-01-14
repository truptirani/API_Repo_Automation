package api;

import endpoints.urls;
import io.restassured.response.Response;

public class ConfirmationApi {

    public static Response sendScbmlsXml(String xmlPayload) {
        return ApiSpec.baseSpec()
                .contentType("application/xml")
                .body(xmlPayload)
            .when()
                .post(urls.CONFIRMATION_API)
            .then()
                .extract().response();
    }
}
