package spec;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;

import static io.restassured.RestAssured.basic;
import static io.restassured.RestAssured.given;

public class Specifications {

    public static String token = "";
    public static String tokenUser = "";

    public Integer vacationTypeID = 0;

    public final String URL = "http://vacation-dev.lan/api";
    // Запрос спецификация
    public static RequestSpecification requestSpec(String url) {
        RestAssured.authentication = basic("username", "password");
        return new RequestSpecBuilder()
                .setBaseUri(url)
                .setContentType(ContentType.JSON)
                .build();
    }

    // Ответ спецификация на 200 код
    public static ResponseSpecification specResponseOK200() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .build();
    }

    // Ответ спецификация на 201 код
    public static ResponseSpecification specResponseOK201() {
        return new ResponseSpecBuilder()
                .expectStatusCode(201)
                .build();
    }

    // Ответ спецификация на 204 код
    public static ResponseSpecification specResponseOK204() {
        return new ResponseSpecBuilder()
                .expectStatusCode(204)
                .build();
    }

    // Ответ спецификация на 400 код
    public static ResponseSpecification specResponseError400() {
        return new ResponseSpecBuilder()
                .expectStatusCode(400)
                .build();
    }
    public static ResponseSpecification specResponseError401() {
        return new ResponseSpecBuilder()
                .expectStatusCode(401)
                .build();
    }

    public static ResponseSpecification specResponseError403() {
        return new ResponseSpecBuilder()
                .expectStatusCode(403)
                .build();
    }

    // Ответ спецификация на 404 код
    public static ResponseSpecification specResponseError404() {
        return new ResponseSpecBuilder()
                .expectStatusCode(404)
                .build();
    }

    // Ответ спецификация на  код
    public static ResponseSpecification specResponseUnique(int status) {
        return new ResponseSpecBuilder()
                .expectStatusCode(status)
                .build();
    }

    public static void installSpecification(RequestSpecification request, ResponseSpecification response) {
        RestAssured.requestSpecification = request;
        RestAssured.responseSpecification = response;
    }

    //----------------------------------------------------------------------------------------
    public String RandomString(int n) {

        int length = n;
        boolean useLetters = true;
        boolean useNumbers = false;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);

        return generatedString;
    } // генерация случайной строки

    @BeforeTest
    public void setFilter() {
        RestAssured.filters(new AllureRestAssured());
    }
    /**
     * Получение токена Admin перед выполнением тестов
     * @throws JSONException
     */
    @BeforeClass
    public void testOAuthWithAdmin() throws JSONException {
        Response response =
                (Response) given()
                        .auth().preemptive().basic("core", "d11e83a3-95cc-460c-9289-511d36d3e3fb")
                        .contentType("application/x-www-form-urlencoded").log().all()
                        .formParam("grant_type", "password")
                        .formParam("username", "admin")
                        .formParam("password", "admin")
                        .when()
                        .post("http://keycloak-dev.lan/auth/realms/freeipa-realm/protocol/openid-connect/token");
             /*           .then().log().all();
        System.out.println(response);*/

        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        String accessToken = jsonObject.get("access_token").toString();
        String tokenType = jsonObject.get("token_type").toString();
        System.out.println("Oauth Token with type " + tokenType + "   " + accessToken);
        token = accessToken;
    }
    @BeforeClass
    public void testOAuthWithUser() throws JSONException {
        Response response =
                (Response) given()
                        .auth().preemptive().basic("core", "d11e83a3-95cc-460c-9289-511d36d3e3fb")
                        .contentType("application/x-www-form-urlencoded").log().all()
                        .formParam("grant_type", "password")
                        .formParam("username", "konstantin.kostylev@irlix.ru")
                        .formParam("password", "P@ssw0rd4323")
                        .when()
                        .post("http://keycloak-dev.lan/auth/realms/freeipa-realm/protocol/openid-connect/token");
             /*           .then().log().all();
        System.out.println(response);*/

        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        String accessToken = jsonObject.get("access_token").toString();
        String tokenType = jsonObject.get("token_type").toString();
        System.out.println("Oauth Token with type " + tokenType + "   " + accessToken);
        tokenUser = accessToken;
    }


}

