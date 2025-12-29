package api;

import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class ApiSpec  {
     public static RequestSpecification baseSpec() {
        return given()
                .contentType(ContentType.JSON)
                .header("X-API-KEY","test-key");
    }
    
}
