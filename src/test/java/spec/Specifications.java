package spec;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.lang.RandomStringUtils;

import static io.restassured.RestAssured.basic;

public class Specifications {

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
    }




}

