package api;

import static io.restassured.RestAssured.given;
import io.restassured.specification.RequestSpecification;

public class ApiSpec  {
     public static RequestSpecification baseSpec() {
        return given()
                .header("X-API-KEY","test-key")
                .contentType("application/json");
    }

    public static RequestSpecification baseSpecForPdf() {
        return given()
                .header("X-API-KEY","test-key")
                .contentType("application/pdf");
    }
    
}
